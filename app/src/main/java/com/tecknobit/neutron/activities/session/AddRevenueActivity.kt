package com.tecknobit.neutron.activities.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.neutron.activities.session.MainActivity.Companion.currency
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.displayFontFamily

class AddRevenueActivity : ComponentActivity() {

    private lateinit var revenueValue: MutableState<String>

    private val digits : ArrayDeque<Int> = ArrayDeque()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            revenueValue = remember { mutableStateOf("0") }
            NeutronTheme {
                Scaffold (
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            },
                            title = {}
                        )
                    },
                ) {
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 32.dp,
                                        end = 32.dp
                                    ),
                                text = "${revenueValue.value}$currency",
                                color = Color.White,
                                fontFamily = displayFontFamily,
                                fontSize = 50.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Card (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 275.dp
                                ),
                            shape = RoundedCornerShape(
                                topStart = 50.dp,
                                topEnd = 50.dp
                            )
                        ) {
                            Keyboard()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Keyboard() {
        Column (
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn {
                repeat(3) { j ->
                    item {
                        Row (
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            repeat(3) { i ->
                                NumberKeyboardButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    number = (j * 3) + i + 1
                                )
                            }
                        }
                    }
                }
                item {
                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            modifier = Modifier
                                .padding(
                                    start = 25.dp
                                ),
                            action = {
                                if(digits.isNotEmpty()) {
                                    if(revenueValue.value.last() == '.')
                                        revenueValue.value = revenueValue.value.removeSuffix(".")
                                    else {
                                        val digit = digits.removeLast()
                                        revenueValue.value = if(revenueValue.value.contains("."))
                                            revenueValue.value.removeSuffix(digit.toString())
                                        else
                                            ((revenueValue.value.toInt() - digit) / 10).toString()
                                    }
                                } else if(revenueValue.value.last() == '.')
                                    revenueValue.value = revenueValue.value.removeSuffix(".")
                            },
                            icon = Icons.AutoMirrored.Filled.Backspace
                        )
                        NumberKeyboardButton(
                            modifier = Modifier
                                .padding(
                                    start = 45.dp
                                )
                                .weight(1f),
                            number = 0
                        )
                        KeyboardButton(
                            modifier = Modifier
                                .padding(
                                    start = 15.dp
                                )
                                .weight(1f),
                            onClick = {
                                if(!revenueValue.value.contains("."))
                                    revenueValue.value += "."
                            },
                            text = ".",
                            fontSize = 50.sp
                        )
                    }
                }
            }
        }
    }

    @Wrapper
    @Composable
    private fun NumberKeyboardButton(
        modifier: Modifier,
        number: Int
    ) {
        KeyboardButton(
            modifier = modifier,
            onClick = {
                revenueValue.value = if(revenueValue.value.contains(".")) {
                    if(revenueValue.value.split(".")[1].length < 2)
                        revenueValue.value + number
                    else
                        revenueValue.value
                } else
                        (revenueValue.value.toInt() * 10 + number).toString()
                digits.add(number)
            },
            text = number.toString()
        )
    }

    @Composable
    private fun KeyboardButton(
        modifier: Modifier,
        onClick: () -> Unit,
        text: String,
        fontSize: TextUnit = 45.sp
    ) {
        TextButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(
                text = text,
                fontSize = fontSize
            )
        }
    }

    @Composable
    private fun ActionButton(
        modifier: Modifier,
        action: () -> Unit,
        icon: ImageVector
    ) {
        Button(
            modifier = modifier
                .size(75.dp)
                .clip(CircleShape),
            onClick = action,
        ) {
            Icon(
                modifier = Modifier
                    .size(25.dp),
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

}