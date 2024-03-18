package com.example.weather3h.files

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather3h.R
import com.example.weather3h.ui.theme.Pink80
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import com.example.weather3h.datas.Dannie
import com.example.weather3h.ui.theme.Pink40
import com.google.accompanist.pager.HorizontalPager
import org.json.JSONArray
import org.json.JSONObject


@Composable

fun mainCard(cday:MutableState<Dannie>,onClickSync:()->Unit,onClickSearch:()-> Unit) {

    Column(modifier = Modifier.padding(7.dp)) {

        Card(
            modifier = Modifier.fillMaxWidth().alpha(0.68f),
            colors = CardDefaults.cardColors(
                containerColor = Pink40,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp, start = 7.dp),
                            text = cday.value.time,
                            style = TextStyle(fontSize = 15.sp),
                            color = White

                        )

                        AsyncImage(
                            model = if(cday.value.time in "00:00".."12:00")
                                "https://cdn.weatherapi.com/weather/64x64/night/113.png"
                            else "https://cdn.weatherapi.com/weather/64x64/day/119.png" ,
                            contentDescription = "Vay",
                            modifier = Modifier.padding(top = 1.dp, start = 1.dp).size(40.dp)

                        )

                    }
                    Text(text=cday.value.city,
                        style = TextStyle(fontSize = 30.sp),
                        color = White

                    )
                    Text(text=if(cday.value.currTemp.isNotEmpty())
                        "${cday.value.currTemp.toFloat().toInt()} ℃"
                        else "${cday.value.maxT.toFloat().toInt()}℃/${cday.value.minT.toFloat().toInt()}℃" ,
                        style = TextStyle(fontSize = 50.sp),
                        color = White

                    )
                    Text(text=cday.value.conditional,
                        style = TextStyle(fontSize = 20.sp),
                        color = White

                    )
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = SpaceBetween){
                        IconButton(onClick = {
                            onClickSearch.invoke()

                        }){
                            Icon(painter = painterResource(id = R.drawable.search),contentDescription= "sky",tint = White)
                        }
                        Text(modifier = Modifier.padding(top = 9.dp, start = 3.dp),
                            text="${cday.value.maxT.toFloat().toInt()}℃/${cday.value.minT.toFloat().toInt()}℃",
                            style = TextStyle(fontSize = 20.sp),
                            color = White

                        )
                        IconButton(onClick = {
                                        onClickSync.invoke()

                        }){
                            Icon(painter = painterResource(id = R.drawable.sync),contentDescription= "sky",tint = White)
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(dayl: MutableState<List<Dannie>>,cday:MutableState<Dannie>) {
    val tabL = listOf("HOURS", "DAY")
    val ps = rememberPagerState()
    val tabI = ps.currentPage
    val cs = rememberCoroutineScope()

    Column(modifier = Modifier.padding(start = 5.dp,end=5.dp).clip(RoundedCornerShape(4.dp))) {
        TabRow(selectedTabIndex = tabI,
            indicator = {
                    pos -> TabRowDefaults.Indicator(modifier=Modifier.pagerTabIndicatorOffset(ps, pos))
            },
            backgroundColor = Pink80,contentColor = White,modifier = Modifier.alpha(0.8f)
        ) {
            tabL.forEachIndexed { index, text ->
                Tab(selected = false, onClick = {
                    cs.launch{
                        ps.animateScrollToPage(index)
                    }
                },
                    text = {
                        Text(text = text,color = White)
                    }
                )
            }
        }

        HorizontalPager(count = tabL.size,state=ps,modifier= Modifier.weight(1.0f)){
            index ->
                val list = when(index){
                    0 -> byHours(cday.value.hours)
                    1 -> dayl.value
                    else -> dayl.value
                }
                Mlist(list,cday)
            }
        }
    }

fun byHours(hours:String):List<Dannie>{
    if(hours.isEmpty())
        return listOf()
    val arrh = JSONArray(hours)
    val arr = ArrayList<Dannie>()
    for(i in 0 until arrh.length()){
        val it = arrh[i] as JSONObject
        arr.add(
            Dannie("",it.getString("time"),it.getString("temp_c").toFloat().toInt().toString()+"℃",
                it.getJSONObject("condition").getString("text"),
                it.getJSONObject("condition").getString("icon"),
                "","","")
        )
    }
    return arr
}

