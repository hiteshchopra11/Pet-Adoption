/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.data.model.Pets
import com.example.androiddevchallenge.data.petsList
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.loadPicture
import com.google.gson.Gson

const val DEFAULT = R.drawable.dog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context: Context = applicationContext
        setContent {
            MyTheme {
                MyScreenContent(context)
            }
        }
    }
}

@Composable
fun MyScreenContent(context: Context) {
    Column(modifier = Modifier.fillMaxHeight()) {
        TopAppBar(
            title = { Text(text = "Pet Adoption App") },
            backgroundColor = Color(0xFF6200EE)
        )
        AppNavigator(context = context)
    }
}

@Composable
fun AppNavigator(context: Context) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "petsList",
        builder = {
            composable("petsList") { PetsList(pets = petsList, navHostController = navController) }
            composable(
                "petDetails/{petJson}",
                arguments = listOf(
                    navArgument("device") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("petJson")?.let { json ->
                    val pet = Gson().fromJson(json, Pets::class.java)
                    PetsDetails(pet = pet, context = context)
                }
            }
        }
    )
}

@Composable
fun PetsList(navHostController: NavHostController, pets: List<Pets>) {
    fun navigateToPet(pet: Pets) {
        val petJson = Gson().toJson(pet)
        navHostController.navigate("petDetails/$petJson")
    }

    MaterialTheme {
        val typography = MaterialTheme.typography

        LazyColumn(
            modifier = Modifier
                .absolutePadding(left = 8.dp, right = 8.dp, bottom = 8.dp)
        ) {
            items(items = pets) { pet ->

                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { navigateToPet(pet = pet) }
                ) {
                    val image = loadPicture(url = pet.image, defaultImage = DEFAULT).value
                    image?.let { img ->
                        Image(
                            painter = BitmapPainter(image = img.asImageBitmap()),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .height(100.dp)
                                .width(100.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            "Name: ${pet.name}",
                            style = typography.h6,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Category: ${pet.category}",
                            style = typography.body1
                        )
                        Text(
                            "Breed: ${pet.breed}",
                            style = typography.body2
                        )
                    }
                }
                Divider(color = Color.Black)
            }
        }
    }
}

@Composable
fun PetsDetails(pet: Pets, context: Context) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            val image = loadPicture(url = pet.image, defaultImage = DEFAULT).value
            image?.let { img ->
                Image(
                    painter = BitmapPainter(image = img.asImageBitmap()),
                    contentDescription = null,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                        .clip(
                            shape = RoundedCornerShape(
                                CornerSize(16.dp),
                                CornerSize(16.dp),
                                CornerSize(16.dp),
                                CornerSize(16.dp)
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Name: ${pet.name}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                ),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Category: ${pet.category}",
                style = TextStyle(fontSize = 20.sp),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                "Breed: ${pet.breed}",
                style = TextStyle(fontSize = 20.sp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Origin: ${pet.origin}",
                style = TextStyle(fontSize = 20.sp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Temperament: ${pet.temperament}",
                style = TextStyle(fontSize = 20.sp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "LifeSpan: ${pet.lifeSpan}",
                style = TextStyle(fontSize = 20.sp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Pet Adopted Successfully!!!!!", Toast.LENGTH_SHORT)
                            .show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Red
                    )
                ) {
                    Text("Adopt Pet")
                }
            }
        }
    }
}
