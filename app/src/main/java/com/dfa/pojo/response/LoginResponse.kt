package com.dfa.pojo.response

data class LoginResponse(
    val token: String,
    val user_email: String,
    val user_nicename: String,
    val user_display_name: String,
    val user_role: String,
    val police_station_id:String="00",
    val message:String

)
/*  {
      "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9zdGdzcC5hcHBzbmRldnMuY29tOjkwNDFcL2RydWdmcmVlIiwiaWF0IjoxNTg1MDYxMTYxLCJuYmYiOjE1ODUwNjExNjEsImV4cCI6MTU4NTY2NTk2MSwiZGF0YSI6eyJ1c2VyIjp7ImlkIjoiMSJ9fX0.gYEcxbxxMEdgU3RvZkcXR7I1pHDwAzgP2ski2YLEv3M",
      "user_email": "vk@gmail.com",
      "user_nicename": "designmintgraphics-com-au",
      "user_display_name": "admin"
  }*/
