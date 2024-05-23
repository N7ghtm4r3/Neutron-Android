package com.tecknobit.neutron.activities.session.addactivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.tecknobit.neutron.R
import com.tecknobit.neutron.activities.session.MainActivity
import com.tecknobit.neutron.ui.InsertionLabelBadge
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronTextField
import com.tecknobit.neutron.ui.theme.displayFontFamily
import com.tecknobit.neutroncore.records.revenues.RevenueLabel

class AddRevenuesActivity : AddRevenueActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AddRevenueUI() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            revenueTitle = remember { mutableStateOf("") }
            revenueDescription = remember { mutableStateOf("") }
            val currentDate = remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            val displayDatePickerDialog = remember { mutableStateOf(false) }
            val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
            val currentTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            val timePickerState = getTimePickerState()
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        top = 25.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 25.dp,
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                var isProjectRevenue by remember { mutableStateOf(false) }
                SingleChoiceSegmentedButtonRow (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { showKeyboard.value = !showKeyboard.value}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    SegmentedButton(
                        selected = !isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.general_revenue)
                        )
                    }
                    SegmentedButton(
                        selected = isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.project)
                        )
                    }
                }
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = revenueTitle,
                    label = R.string.title
                )
                if(!isProjectRevenue) {
                    NeutronTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 250.dp
                            ),
                        value = revenueDescription,
                        label = R.string.description,
                        isTextArea = true
                    )
                    Labels()
                }
                TimeInfoSection(
                    dateTitle = R.string.insertion_date,
                    date = currentDate,
                    displayDatePickerDialog = displayDatePickerDialog,
                    dateState = dateState,
                    timeTitle = R.string.insertion_time,
                    time = currentTime,
                    displayTimePickerDialog = displayTimePickerDialog,
                    timePickerState = timePickerState
                )
                NeutronButton(
                    onClick = {
                        // TODO: MAKE THE REQUEST THEN
                        navBack()
                    },
                    text = if(isProjectRevenue)
                        R.string.add_project
                    else
                        R.string.add_revenue
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Labels() {
        val labels = remember { mutableStateListOf<RevenueLabel>() }
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.labels),
                fontSize = 18.sp
            )
            LazyRow (
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 5.dp,
                    top = 5.dp,
                    bottom = 5.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if(labels.size < 5) {
                    stickyHeader {
                        val showAddLabel = remember { mutableStateOf(false) }
                        FloatingActionButton(
                            modifier = Modifier
                                .size(35.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(
                                size = 10.dp
                            ),
                            onClick = { showAddLabel.value = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                        AddLabel(
                            showAddLabel = showAddLabel,
                            labels = labels
                        )
                    }
                }
                items(
                    items = labels,
                    key = { it.text }
                ) { label ->
                    InsertionLabelBadge(
                        labels = labels,
                        label = label
                    )
                }
            }
            HorizontalDivider()
        }
    }

    @Composable
    private fun AddLabel(
        showAddLabel: MutableState<Boolean>,
        labels: SnapshotStateList<RevenueLabel>
    ) {
        val controller = rememberColorPickerController()
        if(showAddLabel.value) {
            Dialog(
                onDismissRequest = { showAddLabel.value = false },
            ) {
                Card (
                    shape = RoundedCornerShape(
                        size = 15.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp
                            ),
                        text = stringResource(R.string.add_label),
                        fontFamily = displayFontFamily,
                        fontSize = 20.sp
                    )
                    Column (
                        modifier = Modifier
                            .padding(
                                all = 16.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val labelText = remember { mutableStateOf("") }
                        var hexColor by remember { mutableStateOf("#FFFFFF") }
                        InsertionLabelBadge(
                            modifier = Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(
                                        size = 5.dp
                                    )
                                ),
                            label = RevenueLabel(
                                labelText.value.ifEmpty { stringResource(R.string.label_text) },
                                hexColor
                            )
                        )
                        NeutronTextField(
                            modifier = Modifier,
                            value = labelText,
                            label = R.string.label_text
                        )
                        HsvColorPicker(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp
                                )
                                .fillMaxWidth()
                                .height(200.dp),
                            controller = controller,
                            initialColor = Color.White,
                            onColorChanged = { colorEnvelope -> hexColor = colorEnvelope.hexCode }
                        )
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showAddLabel.value = false }
                            ) {
                                Text(
                                    text = stringResource(R.string.dismiss)
                                )
                            }
                            TextButton(
                                onClick = {
                                    labels.add(
                                        RevenueLabel(
                                            labelText.value,
                                            hexColor
                                        )
                                    )
                                    showAddLabel.value = false
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.confirm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun navBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}