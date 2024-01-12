package com.aca.people

 fun getMockData(): List<People> {
    val list = ArrayList<People>()
    for (i in 1..10) {
        list.add(People(
            "NAME "+i,
            "sample_email_"+i+"@email.com",
        ))
    }
    return list
}