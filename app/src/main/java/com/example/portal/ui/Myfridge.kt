package com.example.portal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.portal.ui.theme.BrightGreen
import com.example.portal.ui.theme.LightGreen
import com.example.portal.ui.theme.Gray800
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.portal.R
import com.example.portal.dto.requests.auth.BaseResponse
import com.example.portal.dto.requests.auth.SessionManager
import com.example.portal.dto.responses.FridgeResponse
import com.example.portal.dto.responses.UserResponse
import com.example.portal.repositories.UserRepository
import com.example.portal.ui.theme.White
import kotlinx.coroutines.launch


@Composable
fun MyFridge(accessToken: String){

    val fridgeRespo = UserRepository()

    val loading = remember {
        mutableStateOf(true)
    }

    val Fridge = remember {
        mutableStateOf(emptyList<FridgeResponse>())
    }

    produceState<List<FridgeResponse>>(
        initialValue = emptyList(),
        producer = {
            val response = fridgeRespo.getFridgeItems(accessToken)

            value = response?.body()!!

            loading.value = false
            Fridge.value = value
        })


    Column(
        modifier = Modifier.fillMaxSize(),

        ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp),
            shape = RoundedCornerShape(15.dp),
            BrightGreen
        ) {
            Box(modifier = Modifier
                .size(0.dp, 50.dp)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                Text(text = "Fridge", style = MaterialTheme.typography.h5)
            }


        }

        if(loading.value){
            Text(text = "Loading...", style = MaterialTheme.typography.h5, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        }
        
        LazyColumn(){
            itemsIndexed(Fridge.value){ index, item ->
                FridgeItem(item.product.photo,item.product.name,item.amount,"kg")

            }
        }


    }
}

@Composable
fun FridgeItem(picture: String, name: String, capacity: Int, capacitySymbol: String){
    var capacitynew = remember {
        mutableStateOf(capacity)
    }
    var isreal = remember {
        mutableStateOf(true)
    }
    var deletedDalog = remember {
        mutableStateOf(false)
    }
    var editDialog = remember {
        mutableStateOf(false)
    }
    if (isreal.value){
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = RoundedCornerShape(15.dp),
            LightGreen) {
            Box(modifier = Modifier
                .size(0.dp, 100.dp)
                .fillMaxWidth(),
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(painter = rememberAsyncImagePainter(picture),
                        contentDescription = "fridgeitem",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(80.dp)
                            .clip(CircleShape),
                    )
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                    ) {
                        Text(text = name, modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp), maxLines = 2, style = MaterialTheme.typography.h6)
                        Text(text = "Amount: ${capacitynew.value}", modifier =  Modifier.fillMaxWidth(0.8f), style = MaterialTheme.typography.h6)
                    }
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = {
                            editDialog.value = true;
                        }) {
                            Icon(Icons.Filled.Edit,null)
                        }
                        IconButton(onClick = {
                            deletedDalog.value = true
                        }) {
                            Icon(Icons.Filled.Clear,null)
                        }
                    }
                }
                if(deletedDalog.value){
                    showDeleteDialog(
                        onDismiss = {
                            deletedDalog.value = false
                        },
                        onConfirm = {
                            isreal.value = false;
                            //отута вставити видалення з бази данних
                            deletedDalog.value = false
                        },
                        name = name
                    )
                }
                if(editDialog.value){
                    showEditDialog(
                        onDismiss = {
                            editDialog.value = false
                        },
                        {
                            capacityFromDialog -> capacityFromDialog
                            capacitynew.value = capacityFromDialog
                            //отута вставити оновлення бази данних
                            editDialog.value = false
                        },
                        name = name,
                        capacity = capacitynew.value
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showDeleteDialog(
    onDismiss:() -> Unit,
    onConfirm:() -> Unit,
    name: String
){
    Dialog(
        onDismissRequest = {onDismiss()},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Card(
            elevation = 5.dp,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .width(250.dp)
        ) {
            Column(

            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    BrightGreen
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(text = "${name} will be removed from your fridge", style = MaterialTheme.typography.h5, textAlign = TextAlign.Center)
                    }


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                                  onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                           backgroundColor = Gray800
                        ),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.h6
                        )
                    }
                    Button(
                        onClick = {
                                  onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = BrightGreen
                        ),
                        shape = RoundedCornerShape(15.dp),

                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showEditDialog(
    onDismiss:() -> Unit,
    onConfirm:(capacityFromDialog: Int) -> Unit,
    name: String,
    capacity: Int
){
    var capacitynew = remember {
        mutableStateOf(capacity.toString())
    }
    var colorTextFieldBorder = remember {
        mutableStateOf(Color.Black)
    }
    var widhtTextFieldBorder = remember {
        mutableStateOf(-1.dp)
    }
    var errorMessage = remember {
        mutableStateOf("")
    }
    var length = 0;
    Dialog(
        onDismissRequest = {onDismiss()},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Card(
            elevation = 5.dp,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .width(250.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    BrightGreen
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(text = "${name}", style = MaterialTheme.typography.h5, textAlign = TextAlign.Center)
                    }

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(

                        shape = RoundedCornerShape(5.dp),
                        value = capacitynew.value,
                        onValueChange = {
                            length = it.length

                            capacitynew.value = it
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .size(100.dp, 50.dp)
                            .border(
                                width = widhtTextFieldBorder.value,
                                color = colorTextFieldBorder.value,
                                shape = RoundedCornerShape(5.dp),
                            ),
                        textStyle = TextStyle(fontSize = 13.sp)
                        )

                }
                if(errorMessage.value!=""){
                    Text(
                        text = errorMessage.value,
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,

                        )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Gray800
                        ),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.h6
                        )
                    }
                    Button(
                        onClick = {
                            if(length > 7){
                                colorTextFieldBorder.value = Color.Red
                                widhtTextFieldBorder.value = 2.dp
                                errorMessage.value = "The line is too long"
                            }else{
                                try {
                                    onConfirm(capacitynew.value.toInt())
                                }catch (e: java.lang.NumberFormatException){
                                    colorTextFieldBorder.value = Color.Red
                                    widhtTextFieldBorder.value = 2.dp
                                    errorMessage.value = "The line does not contain a number"
                                }
                            }


                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = BrightGreen
                        ),
                        shape = RoundedCornerShape(15.dp),

                        ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.h6,
                        )
                    }
                }
            }
        }
    }
}