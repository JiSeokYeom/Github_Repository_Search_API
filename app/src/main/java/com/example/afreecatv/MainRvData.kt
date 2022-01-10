package com.example.afreecatv

data class MainRvData(val items: List<Item>)
data class Item (val owner : Owner,val full_name : String, val language : String )
data class Owner(val avatar_url : String)



