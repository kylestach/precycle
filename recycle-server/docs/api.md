# API

### Get User Stats
`GET /user_stats?user={user_id}`

Example Response:

```json
{
  "email":"wgulian3@gatech.edu",
  "github":"https://github.com/pear0",
  "major":"Computer Science",
  "name":"William Gulian",
  "school":"Georgia Institute of Technology",
  "school-year":"First year",
  "tags":[
    "hackgt",
    "workshop_robotarium",
    "food_saturday_breakfast",
    "workshop_semantic_search",
    "food_saturday_lunch1",
    "food_saturday_lunch2"
   ],
   "website":"https://willgulian.com/",
   "disposal_types": [
      {
        "name": "compost",
        "points": 76
      },
      { 
        "name": "trash",
        "points": 12
      },
      {
        "name": "plastic",
        "points": 23
      }
   ],
   "total_points": 111,
   "level": 5,
   "level_required_points": 504
}
```

### Detect Image
`POST /image_detect`

Url Form Encoded Body:

| Name | Description |
|------|-------------|
| user | User id of the user from their badge or id |
| kiosk_id | ID of the kiosk they user is located at |
| image_data | base64 encoded image to be detected |


Example Response:

```json
{
  "type": "compost",
  "points": 5,
  "description": "This is how you recycle compost. Lorem ipsum dolor iset..."
}
```

### Get Leaderboard
`GET /leaderboard`

Example Response:

```json
{
  "leaderboard": [
    {
      "name": "William Gulian",
      "points": 111
    },
    {
      "name": "Kyle Stachowicz",
      "points": 78
    },
    {
      "name": "Simar",
      "points": 54
    },
    {
      "name": "Ananth Dandibhotla",
      "points": -5
    }
  ]
}
```
