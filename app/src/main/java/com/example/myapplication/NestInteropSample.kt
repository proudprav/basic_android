package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import kotlin.math.roundToInt

//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun ComposeInCooperatingViewNestedScrollInteropSample() {
//    val nestedSrollInterop = rememberNestedScrollInteropConnection()
//    // Add the nested scroll connection to your top level @Composable element
//    // using the nestedScroll modifier.
//    Column(
//        modifier = Modifier
//            .nestedScroll(nestedSrollInterop)
//            .fillMaxSize()
//            .verticalScroll(
//                rememberScrollState()
//            )
//    ) {
//        Text(text = "hello")
//        LazyColumn(modifier = Modifier.nestedScroll(nestedSrollInterop)) {
//            items(20) { item ->
//                Box(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .height(56.dp)
//                        .fillMaxWidth()
//                        .background(Color.Gray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(item.toString())
//
//
//                }
//            }
//        }
//    }
//}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposeInCooperatingViewNestedScrollInteropSample() {
    val nestedSrollInterop = rememberNestedScrollInteropConnection()
    // Add the nested scroll connection to your top level @Composable element
    // using the nestedScroll modifier.
    Column (modifier = Modifier.fillMaxSize()){
        Box(
            modifier = Modifier
                .padding(16.dp)
                .height(100.dp)
                .fillMaxWidth()
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "top progress")
        }
        LazyColumn(modifier = Modifier.nestedScroll(nestedSrollInterop)) {
            item {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "ueuwhfuhfu")
                }
            }
            itemsIndexed(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 23, 45, 6)) { index, item ->

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.toString())
                }
            }


        }
    }
}