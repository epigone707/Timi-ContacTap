from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
import json
import os, time
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt
from django.db import connection
from google.oauth2 import id_token
from google.auth.transport import requests
import psycopg2
import psycopg2.extras


# Helper function for checking idToken on requests
# Returns userId on sucess, empty string on fail
def validateUser(idToken, clientId):
    userId = ''
    cursor = connection.cursor()

    cursor.execute(
        'SELECT userid FROM logins WHERE idtoken = %s',
        (idToken,)
    )

    validLoginData = cursor.fetchone()

    if len(validLoginData) > 0:
        userId = validLoginData[0]

    # Need to renew token
    if userId is None:
        try:
            idInfo = id_token.verify_oauth2_token(idToken, requests.Request(), clientId)

            userId = idInfo['sub']

            cursor.execute(
                'SELECT * FROM logins WHERE userid = %s',
                (userId,)
            )
            validUser = cursor.rowcount

            # No such user
            if validUser == 0:
                return ''

            cursor.execute(
                'UPDATE logins SET idtoken = %s WHERE userid = %s',
                (idToken, userId)
            )
        except ValueError:
            # Invalid token
            return ''

    # Token, user are both valid
    return userId

@csrf_exempt
def login(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    clientId = json_data['clientId']   # the front end app's OAuth 2.0 Client ID
    idToken = json_data['idToken']     # user's OpenID ID Token, a JSon Web Token (JWT)
    displayName = json_data['displayName']

    # Specify the CLIENT_ID of the app that accesses the backend:
    idInfo = id_token.verify_oauth2_token(idToken, requests.Request(), clientId)

    # ID token is valid. Get the user's Google Account ID from the decoded token.
    userId = idInfo['sub']

    cursor = connection.cursor()
    cursor.execute(
        'SELECT * FROM logins WHERE userid = %s',
        (userId,)
    )

    hasLogin = cursor.rowcount
    
    if hasLogin == 1:
        cursor.execute(
            'UPDATE logins SET idtoken = %s WHERE userid = %s',
            (idToken, userId)
        )
    else:
        cursor.execute(
             'INSERT INTO logins (userid, idtoken, username) VALUES (%s, %s, %s)',
            (userId, idToken, displayName)
         )
    
    return JsonResponse({})
    #except ValueError:
    # Invalid token
    #return HttpResponse(status=401)

# Get ContactInfo: /contactinfo/
@csrf_exempt
def getcontactinfo(request):
    if request.method != 'POST':
       return HttpResponse(status=400)
    
    json_data = json.loads(request.body)

    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    cursor = connection.cursor()
    # Get user's contact info, split across these three tables
    cursor.execute(
        'SELECT * FROM contactinfo c '
        'INNER JOIN basicinfo b ON c.basicinfoid = b.basicinfoid '
        'INNER JOIN socialinfo s on c.socialinfoid = s.socialinfoid '
        'WHERE userid = %s;',
        (userId,)
    )
    #rowHeaders = [x[0] for x in cursor.description]
    #fetchedData = cursor.fetchone()
    #contactInfo = dict(zip(rowHeaders, fetchedData))
    contactInfo = cursor.fetchone()

    contactInfoDict = {
        'name': contactInfo[4],
        'imageUrl': contactInfo[5],
        'personalEmail': contactInfo[6],
        'businessEmail': contactInfo[7],
        'personalPhone': contactInfo[8],
        'businessPhone': contactInfo[9],
        'otherPhone': contactInfo[10],
        'bio': contactInfo[11],
        'instagram': contactInfo[13],
        'snapchat': contactInfo[14],
        'twitter': contactInfo[15],
        'linkedIn': contactInfo[16],
        'hobbies': contactInfo[17],
        'other': contactInfo[18]
    }

    response = {}
    response['contactInfo'] = contactInfoDict

    return JsonResponse(response)


# Create/POST ContactInfo: /contactinfo/create/
@csrf_exempt
def createcontactinfo(request):
    if request.method != 'POST':
        return HttpResponse(status=400)
    
    json_data = json.loads(request.body)

    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    name = json_data['name']
    personalEmail = json_data['personalEmail']
    businessEmail = json_data['businessEmail'] or None
    personalPhone = json_data['personalPhone'] or None
    businessPhone = json_data['businessPhone'] or None
    otherPhone = json_data['otherPhone'] or None
    bio = json_data['bio'] or None
    instagram = json_data['instagram'] or None
    snapchat = json_data['snapchat'] or None
    twitter = json_data['twitter'] or None
    linkedIn = json_data['linkedIn'] or None
    hobbies = json_data['hobbies'] or None
    other = json_data['other'] or None

    # Do image stuff
    imageUrl = ''

    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = name + ".jpeg"
        fs = FileSystemStorage()
        filename = fs.save(filename, content)
        imageUrl = fs.url(filename)

    cursor = connection.cursor()

    # RETURNING gives us the autogenerated id
    cursor.execute(
        'INSERT INTO basicinfo (name, imageurl, personalemail, '
        'businessemail, personalphone, businessphone, otherphone, bio) VALUES '
        '(%s, %s, %s, %s, %s, %s, %s, %s) RETURNING basicinfoid;',
        (name, imageUrl, personalEmail, businessEmail,
        personalPhone, businessPhone, otherPhone, bio)
    )

    # Take the basicinfoid from the query
    basicInfoId = cursor.fetchone()[0]

    cursor.execute(
        'INSERT INTO socialinfo'
        '(instagram, snapchat, twitter, linkedin, hobbies, other) VALUES'
        '(%s, %s, %s, %s, %s, %s) RETURNING socialinfoid;',
        (instagram, snapchat, twitter, linkedIn, hobbies, other)
    )

    socialInfoId = cursor.fetchone()[0]

    # We create new rows in BasicInfo and SocialInfo, then create the matching set
    # in ContactInfo
    cursor.execute(
        'INSERT INTO contactinfo (userid, basicinfoid, socialinfoid) VALUES (%s, %s, %s);',
        (userId, basicInfoId, socialInfoId)

    )

    return JsonResponse({})

# Update/POST ContactInfo: /contactinfo/update/
@csrf_exempt
def updatecontactinfo(request):
    if request.method != 'POST':
        return HttpResponse(status=400)
    
    json_data = json.loads(request.body)

    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    name = json_data['name']
    personalEmail = json_data['personalEmail']
    businessEmail = json_data['businessEmail'] or None
    personalPhone = json_data['personalPhone'] or None
    businessPhone = json_data['businessPhone'] or None
    otherPhone = json_data['otherPhone'] or None
    bio = json_data['bio'] or None
    instagram = json_data['instagram'] or None
    snapchat = json_data['snapchat'] or None
    twitter = json_data['twitter'] or None
    linkedIn = json_data['linkedIn'] or None
    hobbies = json_data['hobbies'] or None
    other = json_data['other'] or None

    # Do image stuff
    imageUrl = ''

    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = name + ".jpeg"
        fs = FileSystemStorage()
        # Delete previous image
        fs.delete(filename)
        # Save new one
        filename = fs.save(filename, content)
        imageUrl = fs.url(filename)

    cursor = connection.cursor()

    cursor.execute(
        'SELECT * FROM contactinfo WHERE userid = %s;',
        (userId,)
    )

    contactInfoRow = cursor.fetchone()
    contactInfo = {}
    contactInfo['basicInfoId'] = contactInfoRow[1]
    contactInfo['socialInfoId'] = contactInfoRow[2]

    cursor.execute(
        'UPDATE basicinfo SET name = %s, imageurl = %s, personalemail = %s, '
        'businessemail = %s, personalphone = %s, businessphone = %s, '
        'otherphone = %s, bio = %s WHERE basicinfoid = %s;',
        (name, imageUrl, personalEmail, businessEmail, personalPhone,
        businessPhone, otherPhone, bio, contactInfo['basicInfoId'])
    )

    cursor.execute(
        'UPDATE socialinfo SET instagram = %s, snapchat = %s, twitter = %s, '
        'linkedin = %s, hobbies = %s, other = %s WHERE socialinfoid = %s;',
        (instagram, snapchat, twitter, linkedIn,
        hobbies, other, contactInfo['socialInfoId'])
    )

    return JsonResponse({})

@csrf_exempt
def getprofiles(request):
    if request.method != 'POST':
        return HttpResponse(status=400)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    cursor = connection.cursor()
    cursor.execute('SELECT * FROM profiles WHERE userid = %s;', (userId,))
    rows = cursor.fetchall()

    profiles = []

    for row in rows:
        profile = {}
        profile['profileId'] = row[1]
        profile['name'] = row[2]
        profile['description'] = row[3]
        profile['includeBitString'] = row[4]
        profiles.append(profile)

    response = {}
    response['profiles'] = profiles     
    return JsonResponse(response)


#profile/create/
@csrf_exempt
def createprofile(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    includeBitString = json_data['includeBitString']
    profileName = json_data['profileName']
    description = json_data['description']

    cursor = connection.cursor()
    cursor.execute(
        'INSERT INTO profiles (UserId, ProfileName, IncludeBitString, profiledescription) VALUES '
        '(%s, %s, %s, %s) RETURNING ProfileId;', (userId, profileName, includeBitString, description)
    )
    profileId = cursor.fetchone()[0]
    
    response = {}
    response['profileId'] = profileId
    
    return JsonResponse(response)

#profile/update/
@csrf_exempt
def updateprofile(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    profileName = json_data['profileName']
    profileId = json_data['profileId']
    includeBitString = json_data['includeBitString']
    description = json_data['description']
    
    cursor = connection.cursor()
    cursor.execute(
        'UPDATE profiles SET ProfileName = %s, IncludeBitString = %s, profiledescription = %s '
        'WHERE UserId = %s AND ProfileId = %s;',
        (profileName, includeBitString, description, userId, profileId)
    )

    return JsonResponse({'Success message': "Update profile successfully"})

#/profile/delete/
@csrf_exempt
def deleteprofile(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    profileId = json_data['profileId']

    cursor = connection.cursor()
    cursor.execute('DELETE FROM profiles WHERE ProfileId = %s;', (profileId,))

    return JsonResponse({'Success message': "Delete profile successfully"})

#/connection/create/
@csrf_exempt
def createconnection(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    profileId = json_data['profileId']
    location = json_data['location']
    time = json_data['time']
    newIncludeBitString = json_data['newIncludeBitString']

    cursor = connection.cursor()
    cursor.execute(
        'INSERT INTO connections (userid, profileid, timeshared, locationshared, newincludebitstring) '
        'VALUES (%s, %s, %s, %s, %s);', (userId, profileId, time, location, newIncludeBitString)
    )

    return JsonResponse({})

#/connection/delete/
@csrf_exempt
def deleteconnection(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    
    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    profileId = json_data['profileId']

    cursor = connection.cursor()
    cursor.execute('DELETE connections WHERE userid = %s AND profileid = %s;', (userId, profileId))

    return JsonResponse({})

#/connections/
@csrf_exempt
def getconnections(request):
    if request.method != 'POST':
        return HttpResponse(status=404)

    json_data = json.loads(request.body)
    
    idToken = json_data['idToken']
    clientId = json_data['clientId']
    userId = validateUser(idToken, clientId)

    if userId is None:
        return HttpException(status=401)

    cursor = connection.cursor()

    #Do join between connections table and profiles table
    #c.userId means my receiver's userId, p.userId means sender's userId
    cursor.execute(
            'SELECT * FROM connections c INNER JOIN profiles p'
            ' USING (profileid)'                                 #avoid join based on two userIds in two tables
            " WHERE c.userId = %s;", (userId,))

    #For each row of joining result, 
    #     check if newincludebitstring is null
    #     if it is null, extract information from (BasicInfo join ContactInfo join SocialInfo) based on includebitstring column in profile table
    #     if it is not null, extract information from (BasicInfo join ContactInfo join SocialInfo) based on newincludebitstring column in connections table
    rows_connection_join_profiles = cursor.fetchall()

    #all profiles in response (that contain extracted actual info)
    all_profiles_in_response = []

    fourteen_information_name_in_response = ('name', 'imageUrl', 'personalEmail', 'businessEmail', 'personalPhone',
        'businessPhone', 'otherPhone', 'bio', 'instagram', 'snapchat',
        'twitter', 'linkedIn', 'hobbies', 'other', 
        )
   
    '''
    fourteen_information_name_in_postgresql_database = ('name', 'imageUrl', 'personalEmail', 'businessEmail', 'personalPhone',
        'businessPhone', 'otherPhone', 'bio', 'instagram', 'snapchat',
        'twitter', 'linkedIn', 'hobbies', 'other', 
        )     #TODO: make sure it is align with database/github wiki
    '''

    fourteen_information_name_in_postgresql_database = (4, 5, 6, 7, 8,
        9, 10, 11, 13, 14,
        15, 16, 17, 18,
        )

    for row in rows_connection_join_profiles:
        profile_in_response = {}

        #Do BasicInfo join ContactInfo join SocialInfo
        cursor2 = connection.cursor()
        cursor2.execute(
            'SELECT * FROM contactinfo c '
            'INNER JOIN basicinfo b ON c.basicinfoid = b.basicinfoid '
            'INNER JOIN socialinfo s on c.socialinfoid = s.socialinfoid '
            "WHERE userid = %s;",
            (row[5],)             #maybe TODO: maybe I need to change to the index that should I use in p.userId (i.e. sender/other's userId)
        )
        row_BasicInfo_join_ContactInfo_join_SocialInfo = cursor2.fetchone()

        if row[4] == "":
            for i in range(len(row[8])):     #iterate through every bit of includebitstring
                if row[8][i] == '1':
                    profile_in_response[fourteen_information_name_in_response[i]] = row_BasicInfo_join_ContactInfo_join_SocialInfo[fourteen_information_name_in_postgresql_database[i]]       
        else:
            for i in range(len(row[4])):     #iterate through every bit of newincludebitstring
                if row[4][i] == '1':
                    profile_in_response[fourteen_information_name_in_response[i]] = row_BasicInfo_join_ContactInfo_join_SocialInfo[fourteen_information_name_in_postgresql_database[i]]       

        all_profiles_in_response.append(profile_in_response)

    response = {}
    response['connections'] = all_profiles_in_response

    return JsonResponse(response)
