package com.dfa.pojo.response

class CategoriesListResponse {
    var code: Int? = null

    var data: ArrayList<Data>?=null

    var message: String? = null

    class Data{
        var name: String? = null

        var id: String? = null

        var status: String? = null
    }
}
