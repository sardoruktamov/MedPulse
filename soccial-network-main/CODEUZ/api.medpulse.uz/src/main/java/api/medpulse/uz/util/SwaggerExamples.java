package api.medpulse.uz.util;

public class SwaggerExamples {

    public static final String PATIENT_PROFILE_RESPONSE_SUCCESS = """
            {
              "id": "7ffe334d-7c50-47a1-95f9-5d1965eabc76",
                   "fullName": "O'ktamov Sardorbek",
                   "birthDate": "1991-12-24",
                   "gender": "MALE",
                   "photo": "b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg",
                   "bloodGroup": "A_POSITIVE",
                   "weight": 70.0,
                   "height": 182.0,
                   "workingBloodPressure": "120/80",
                   "regionId": 12,
                   "districtId": 179,
                   "address": "Bog'iston MFY,Posbonlar k. 8-uy",
                   "allergies": "chang va tutunga, Paratsetamol",
                   "emergencyContactName": "onam Sobirova Munojatxon",
                   "emergencyContactPhone": "998905846866"
            }
            """;

    public static final String PATIENT_PROFILES_LIST_SUCCESS = """
            [
              {
                   "id": "1acc1c49-8b2f-4cff-ad05-d0191f8e01a9",
                   "fullName": "Zoxidaxon Farxodjonova",
                   "birthDate": "2022-09-26",
                   "gender": "FEMALE",
                   "photo": {
                     "id": "b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg",
                     "originName": "DXM.jpg",
                     "size": 333291,
                     "extension": "jpg",
                     "createdData": "2026-01-29T19:58:39.794174",
                     "url": "http://localhost:8080/api/v1/attach/open/b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg"
                   },
                   "bloodGroup": "A_POSITIVE",
                   "weight": 12.5,
                   "height": 95.0,
                   "workingBloodPressure": "120/80",
                   "regionId": 12,
                   "districtId": 179,
                   "address": "Farovonlik MFY",
                   "allergies": "chang va tutunga, Paratsetamol",
                   "emergencyContactName": null,
                   "emergencyContactPhone": "998915555555"
                 },
                 {
                   "id": "f85815cf-3004-4707-a60b-5c63b129d80d",
                   "fullName": "Sobirova Munojatxon",
                   "birthDate": "1971-09-26",
                   "gender": "FEMALE",
                   "photo": {
                     "id": "b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg",
                     "originName": "DXM.jpg",
                     "size": 333291,
                     "extension": "jpg",
                     "createdData": "2026-01-29T19:58:39.794174",
                     "url": "http://localhost:8080/api/v1/attach/open/b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg"
                   },
                   "bloodGroup": "A_POSITIVE",
                   "weight": 95.5,
                   "height": 160.0,
                   "workingBloodPressure": "120/80",
                   "regionId": null,
                   "districtId": null,
                   "address": null,
                   "allergies": "allergiyasi yoq",
                   "emergencyContactName": null,
                   "emergencyContactPhone": "998915559955"
                 },
                 {
                   "id": "7ffe334d-7c50-47a1-95f9-5d1965eabc76",
                   "fullName": "O'ktamov Sardorbek",
                   "birthDate": "1991-12-24",
                   "gender": "MALE",
                   "photo": {
                     "id": "b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg",
                     "originName": "DXM.jpg",
                     "size": 333291,
                     "extension": "jpg",
                     "createdData": "2026-01-29T19:58:39.794174",
                     "url": "http://localhost:8080/api/v1/attach/open/b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg"
                   },
                   "bloodGroup": "A_POSITIVE",
                   "weight": 70.0,
                   "height": 182.0,
                   "workingBloodPressure": "120/80",
                   "regionId": 12,
                   "districtId": 179,
                   "address": "Bog'iston MFY,Posbonlar k. 8-uy",
                   "allergies": "chang va tutunga, Paratsetamol",
                   "emergencyContactName": "onam Sobirova Munojatxon",
                   "emergencyContactPhone": "998905846866"
                 }
            ]
            """;

    public static final String PATIENT_UPDATE_REQUEST_EXAMPLE = """
            "id": "7ffe334d-7c50-47a1-95f9-5d1965eabc76",
               "fullName": "O'ktamov Sardorbek",
               "birthDate": "1991-12-24",
               "gender": "MALE",
               "photo": {
                 "id": "b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg",
                 "originName": "DXM.jpg",
                 "size": 333291,
                 "extension": "jpg",
                 "createdData": "2026-01-29T19:58:39.794174",
                 "url": "http://localhost:8080/api/v1/attach/open/b6dc146c-0cd2-4b07-b8e5-4adfc4b7480f.jpg"
               },
               "bloodGroup": "A_POSITIVE",
               "weight": 70.0,
               "height": 182.0,
               "workingBloodPressure": "120/80",
               "regionId": 12,
               "districtId": 179,
               "address": "Bog'iston MFY,Posbonlar k. 8-uy",
               "allergies": "chang va tutunga, Paratsetamol",
               "emergencyContactName": "onam Sobirova Munojatxon",
               "emergencyContactPhone": "998905846866"
             }
            """;

    public static final String PATIENT_CREATE_REQUEST_EXAMPLE = """
            {
              "firstName": "Hasan",
              "lastName": "Valiyev",
              "pinfl": "21098765432109",
              "birthDate": "2020-03-21",
              "gender": "MALE",
              "bloodGroup": "O_PLUS",
              "address": "Tashkent, Chilonzor",
              "passportSeria": "AC",
              "passportNumber": "9876543",
              "maritalStatus": "SINGLE",
              "nationality": "Uzbek"
            }
            """;

    public static final String PATIENT_PROFILE_ERROR_EXAMPLE = """
            {
              "statusCode": 400,
              "message": "Ma'lumotni saqlashda xatolik yuz berdi!",
              "path": "/api/v1/patient/family"
            }
            """;

    public static final String PATIENT_NOT_FOUND_ERROR_EXAMPLE = """
            {
              "statusCode": 404,
              "message": "Bemor topilmadi!",
              "path": "/api/v1/patient/123e4567-e89b-12d3-a456-426614174000"
            }
            """;
}
