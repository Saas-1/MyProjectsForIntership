package com.example.weather3h

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather3h.datas.Dannie
import com.example.weather3h.files.TabLayout
import com.example.weather3h.files.dSearch
import com.example.weather3h.files.mainCard
import com.example.weather3h.ui.theme.Weather3hTheme
import org.json.JSONArray
import org.json.JSONObject

const val api = "90916526d26c4ab8a2e201046232911"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dayl = remember{
                mutableStateOf(listOf<Dannie>())
            }
            val cday = remember{
                mutableStateOf(Dannie("","","0.0","","","0.0","0.0",""))
            }
            val dState = remember{
                mutableStateOf(false)
            }
            if(dState.value){
                dSearch(dState,onSubmit={
                    gData(it,this,dayl,cday)
                })
            }
            gData("Minsk",this,dayl,cday)
            Weather3hTheme {
                Image(painter = painterResource(id = R.drawable.skyy),contentDescription= "sky",
                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
                Column{
                    mainCard(cday,onClickSync={
                        gData("Minsk",this@MainActivity,dayl,cday)
                    },onClickSearch = {
                        dState.value = true
                    })
                    TabLayout(dayl,cday)

                }

            }
        }
    }
}

private fun gData(town:String,context: Context,dayl:MutableState<List<Dannie>>,cday:MutableState<Dannie>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$api&q=$town&days=30" +
            "&aqi=no&alerts=no"

    val q = Volley.newRequestQueue(context)
    val sr = StringRequest(
        Request.Method.GET, url,
        { response ->
          val arr = byDay(response)
            cday.value = arr[0]
            dayl.value = arr

        },
        { error ->
            Log.e("Error", error.toString())
        }
    )
    q.add(sr)
}
//@SuppressLint("SuspiciousIndentation")
@SuppressLint("SuspiciousIndentation")
private fun byDay(response:String):List<Dannie>{
   if(response.isEmpty()){
       return listOf()
   }
    val obj = JSONObject(response)
    val arr = ArrayList<Dannie>()
    val city = obj.getJSONObject("location").getString("name")
    val days = obj.getJSONObject("forecast").getJSONArray("forecastday")
    for(i in 0 until days.length()){
      val it = days[i] as JSONObject
        arr.add(Dannie(city,it.getString("date"),"",it.getJSONObject("day").getJSONObject("condition")
            .getString("text"),it.getJSONObject("day").getJSONObject("condition")
            .getString("icon"),it.getJSONObject("day")
            .getString("maxtemp_c"),it.getJSONObject("day")
            .getString("mintemp_c"),it.getJSONArray("hour").toString()))
    }
    arr[0]=arr[0].copy(time=obj.getJSONObject("current").getString("last_updated"),
        currTemp = obj.getJSONObject("current").getString("temp_c"))
    return arr
}
