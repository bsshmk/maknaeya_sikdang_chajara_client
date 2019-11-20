package com.mksoft.maknaeya_sikdang_chajara.testHex

import android.content.res.AssetManager
import com.mksoft.demo.HexData
import com.mksoft.maknaeya_sikdang_chajara.App
import com.naver.maps.geometry.LatLng
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class CaculateShortPath {

    var rate = 110.574 / (111.320 * Math.cos(37.550396 * Math.PI / 180))
    private val center = Point(126.978955, 37.550396)
    var layout = Layout(Layout.pointy, Point(rate * 0.0001, 0.0001), center)
    val hexSet: MutableSet<HexData> = mutableSetOf()
    val am:AssetManager = App.applicationContext().assets
    var reader: BufferedReader
    init {

        reader = BufferedReader(InputStreamReader(am.open("testHexData.txt")))

        while(true){
            val currentLine = reader.readLine() ?: break
            val splitedLine = currentLine.split(" ")
            hexSet.add(HexData(splitedLine[0].toInt(), splitedLine[1].toInt(), splitedLine[2].toInt()))
        }//init set
    }
    fun searchShortestPathList(lat1:Double, lng1:Double, lat2:Double, lng2:Double):MutableList<LatLng>{
        val shortestPath:MutableList<LatLng> = mutableListOf()
        shortestPath.add(LatLng(lat1, lng1))
        val startHex = searchNearHexIDX(lat1, lng1)
        val endHex = searchNearHexIDX(lat2, lng2)
        val shortestHexList = bfsHex(Hex(startHex.q,startHex.r,startHex.s), Hex(endHex.q, endHex.r, endHex.s))
        for(item in shortestHexList) {
            shortestPath.add(convertHexToPoint(Hex(item.q,item.r,item.s)))
        }
        shortestPath.add(LatLng(lat2, lng2))
        return shortestPath
    }
    private fun convertHexToPoint(inputHex:Hex):LatLng{
        return LatLng(layout.hexToPixel(inputHex).y,layout.hexToPixel(inputHex).x)
    }
    private fun bfsHex(startHex:Hex, endHex:Hex):List<HexData>{
        val shortestPath:MutableList<HexData> = mutableListOf()
        val startHexData = HexData(startHex.q,startHex.r,startHex.s)
        val endHexData = HexData(endHex.q,endHex.r,endHex.s)
        if(startHexData == endHexData){
            shortestPath.add(startHexData)
            return shortestPath
        }
        val hexTrace = hashMapOf<HexData, HexData>()
        val bfsQ = ArrayDeque<Hex>()
        bfsQ.add(startHex)
        var separateFlag = false
        val hexDirections = Hex.directions
        while(true){
            if(bfsQ.isEmpty()){
                separateFlag = true
                break
            }
            var finishFlag = false
            val currentQSize = bfsQ.size
            for(i in 0 until currentQSize){
                val currentHex = bfsQ.first
                bfsQ.pop()
                if((currentHex.q == endHex.q) &&(currentHex.r == endHex.r) && (currentHex.s == endHex.s)){
                    finishFlag = true
                    break
                }

                for(hexDirection in hexDirections){
                    val nextHex = currentHex.add(hexDirection)
                    if(hexSet.contains(HexData(nextHex.q,nextHex.r,nextHex.s)) && hexTrace[HexData(nextHex.q,nextHex.r,nextHex.s)] == null){
                        hexTrace[HexData(nextHex.q,nextHex.r,nextHex.s)] = HexData(currentHex.q,currentHex.r,currentHex.s)
                        bfsQ.add(nextHex)
                    }
                }

            }
            if(finishFlag)
                break


        }
        if(separateFlag)
            return shortestPath//도로가 분리되어있는경우
        shortestPath.add(endHexData)
        var currentHex = hexTrace[endHexData]
        shortestPath.add(currentHex!!)
        while(true){
            if(currentHex == startHexData){
                break
            }
            currentHex = hexTrace[currentHex]
            shortestPath.add(currentHex!!)

        }
        shortestPath.reverse()
        return shortestPath
    }

    private fun searchNearHexIDX(latitude:Double, longitude:Double):HexData{
        val hex:FractionalHex = layout.pixelToHex(Point(longitude, latitude))

        val roundHex = FractionalHex.hexLinedraw( hex.hexRound(), hex.hexRound())
        val hexData = HexData(roundHex[0].q, roundHex[0].r,roundHex[0].s)
        if(hexSet.contains(hexData)){
            return hexData
        }
        val hexDirections = Hex.directions
        val currentHex = roundHex[0]
        var resultHex: HexData? = HexData(0,0,0)
        var distance = 987654321
        for(hexDirection in hexDirections){
            var hexDepth = 0
            var nextHex = currentHex
            while(true){
                if(hexDepth==20)
                    break
                nextHex = nextHex.add(hexDirection)

                if(hexSet.contains(HexData(nextHex.q, nextHex.r, nextHex.s))&&distance> kotlin.math.abs(currentHex.distance(nextHex))){
                    distance = currentHex.distance(nextHex)
                    resultHex = HexData(nextHex.q, nextHex.r, nextHex.s)

                    break
                }
                hexDepth++

            }

        }
        return resultHex!!
    }
}