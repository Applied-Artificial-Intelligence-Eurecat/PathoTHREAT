#!/usr/bin/env bash

#api="http://localhost:4567"
api="https://pathothreat.pathocert.eu/api"
#api="http://localhost:4200/api"

# Si falla per jq: command not found ->
# curl -L -o /usr/bin/jq.exe https://github.com/stedolan/jq/releases/latest/download/jq-win64.exe

tokenPathoS=$(curl -X "POST" -H "Content-Type: application/json" -d '{"username": "admin", "password":"root"}' "$api/api/authenticate")

echo $tokenPathoS

tokenPatho=$(echo $tokenPathoS | tac | jq .token | sed 's/"//g')

echo token
echo $tokenPatho

curl -X 'POST' \
  "$api/api/organizations" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": 1,
  "name": "Organization A",
  "description": "Test Organization",
  "actionArea": "AREA1"
}' \
  -H "Authorization: Bearer $tokenPatho"

curl -X 'POST' \
  "$api/api/organizations" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": 2,
  "name": "Organization B",
  "description": "Test Organization",
  "actionArea": "AREA1"
}' \
  -H "Authorization: Bearer $tokenPatho"

curl -X 'POST' \
  "$api/api/users" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"id\": 0,
  \"username\": \"admin\",
  \"password\": \"root\",
  \"userRole\": \"SUPER_ADMIN\",
  \"registrationDate\": 0,
  \"organization\": \"$api/api/organizations/1\"
}"  \
  -H "Authorization: Bearer $tokenPatho"

for num in {1..5}
do
orgId=1
user="user$num"
curl -X 'POST' \
  "$api/api/users" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"id\": $num,
  \"username\": \"$user\",
  \"password\": \"complexPass123\",
  \"userRole\": \"ADMIN\",
  \"registrationDate\": 0,
  \"organization\": \"$api/api/organizations/$orgId\"
}"  \
  -H "Authorization: Bearer $tokenPatho"
done

for num in {5..10}
do
orgId=1
user="user$num"
curl -X 'POST' \
  "$api/api/users" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"id\": $num,
  \"username\": \"$user\",
  \"password\": \"complexPass123\",
  \"userRole\": \"ADMIN\",
  \"registrationDate\": 0,
  \"organization\": \"$api/api/organizations/$orgId\"
}"  \
  -H "Authorization: Bearer $tokenPatho"
done

curl -X 'POST' \
  "$api/api/emergencies" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"id\": 0,
  \"reportDate\": 0,
  \"reportingOrganization\": \"$api/api/organizations/1/\",
  \"reportingUserId\": \"$api/api/users/1/\",
  \"nameReporter\": \"John\",
  \"emergencyType\": \"flood\",
  \"emergencyDescription\": \"Flood\",
  \"waterStateDescription\": \"bad\",
  \"severityLevel\": 3,
  \"nameAreaAffected\": \"string\",
  \"affectedAreaType\": \"string\",
  \"ocupationDescription\": \"string\",
  \"riskAssessment\": \"string\",
  \"actionPlan\": \"string\",
  \"archived\": true,
  \"symptoms\": [\"water-bad\", \"water-color\", \"water-taste\"],
  \"infrastructure\": [\"distribution-broken\", \"sewage-failure\", \"flooding\"],
  \"pathogens\": [\"ecoli\", \"rotovirus\", \"norovirus\"],
  \"impact\": \"\",
  \"control\": \"\"
}"  \
  -H "Authorization: Bearer $tokenPatho"
curl -X 'POST' \
  "$api/api/emergencies" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"id\": 0,
  \"reportDate\": 0,
  \"reportingOrganization\": \"$api/api/organizations/0/\",
  \"reportingUserId\": \"$api/api/users/1/\",
  \"nameReporter\": \"John\",
  \"emergencyType\": \"flood\",
  \"emergencyDescription\": \"Flood\",
  \"waterStateDescription\": \"bad\",
  \"severityLevel\": 3,
  \"nameAreaAffected\": \"string\",
  \"affectedAreaType\": \"string\",
  \"ocupationDescription\": \"string\",
  \"riskAssessment\": \"string\",
  \"actionPlan\": \"string\",
  \"archived\": false,
  \"symptoms\": [\"water-bad\", \"water-color\", \"water-taste\"],
  \"infrastructure\": [\"distribution-broken\", \"sewage-failure\", \"flooding\"],
  \"pathogens\": [\"ecoli\", \"rotovirus\", \"norovirus\"],
  \"impact\": \"\",
  \"control\": \"\"
}"  \
  -H "Authorization: Bearer $tokenPatho"




curl -X 'POST' \
  "$api/api/emergencies" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d  '{"nameReporter":"John Smith","ocupationDescription":"Mosso","emergencyType":"EARTHQUAKE","severityLevel":2,"affectedAreaType":"Street","nameAreaAffected":"Street","emergencyDescription":"Emergency",
  "symptoms": ["water-bad",  "water-taste"],
  "infrastructure": ["sewage-failure"],
  "pathogens": ["ecoli", "rotovirus"], "additionalObservations":"Emergency","archived":false,"reportDate":1646306604455,"reportingUserId":"http://localhost:4567/api/users/0","reportingOrganization":"http://localhost:4567/api/organizations/0","impact":"","control":""}' \
  -H "Authorization: Bearer $tokenPatho"

curl -X 'POST' \
  "$api/api/emergencies" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d  '{"nameReporter":"John John","ocupationDescription":"Johnass","emergencyType":"EARTHQUAKE","severityLevel":3,"affectedAreaType":"City","nameAreaAffected":"Some pipes","emergencyDescription":"Pipes obstructed","waterSmellsBad":true,"colourWaterIsStrange":true,"waterTasteStrange":true,"drinkingWaterDistributionIsBroken":false,"sewageNetworkFailure":true,"flooding":true,"escherichiaColi":true,"norovirus":true,"rotavirus":true,"additionalObservations":"Some additional Observations","archived":false,"impact":"","control":"","reportDate":1649931561518,"reportingUserId":"http://localhost:4567/api/users/0","reportingOrganization":"http://localhost:4567/api/organizations/0"}'  \
  -H "Authorization: Bearer $tokenPatho"


curl -X 'POST' \
  "$api/api/emergencies" \
  -H 'accept: application/hal+json' \
  -H 'Content-Type: application/json' \
  -d  '{
      "reportDate" : 1655110320847,
      "nameReporter" : "John Smith",
      "emergencyType" : "WASTE_WATER",
      "emergencyDescription" : "City debug",
      "waterStateDescription" : null,
      "severityLevel" : 3,
      "nameAreaAffected" : "City",
      "affectedAreaType" : "City",
      "ocupationDescription" : "Reporter",
      "riskAssessment" : null,
      "actionPlan" : null,
      "archived" : false,
      "symptoms" : [ "water-tastes", "water-smells", "water-color" ],
      "pathogens" : [ "ecoli", "norovirus", "rotavirus" ],
      "infrastructure" : [ "distribution-broken", "flooding", "sewage-failure" ],
      "similarityList" : [ ],
      "impact" : "",
      "control" : "",
      "reportingUserId" : "http://localhost:4567/api/users/1",
      "reportingOrganization": "http://localhost:4567/api/organizations/1"
        }

                  }' \
  -H "Authorization: Bearer $tokenPatho"
python3.9 csvtojson.py Document-Merge-Info.csv $api
