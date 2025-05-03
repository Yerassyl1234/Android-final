package com.example.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Stable
data class WashingMachineUIState(
    val id: Int,
    val image: String,
    val number: Int,
    val isActive:Boolean,
    val onFastWashClick: () -> Unit,
    val onManualWashClick: () -> Unit,
    val onCottonWashClick: () -> Unit
)
@Composable
fun WashingMachineCard(
    modifier: Modifier = Modifier,
    state: com.example.ui.booking.WashingMachineUIState
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape= RoundedCornerShape(18.dp))
            .border(width = 4.dp, color = Color.Gray),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    MachineStatus(isActive = state.isActive)

                    Spacer(modifier = Modifier.height(4.dp))

                    WashingMachineImage(
                        modifier = Modifier.size(110.dp),
                        isActive = state.isActive
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Номер машины
                    WashingMachineNumber(
                        number = state.number
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Правая часть с кнопками в колонку
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                BookingButtonFastWash(
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    enabled = !state.isActive,
                    onClick = state.onFastWashClick
                )

                BookingButtonManualWash(
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    enabled = !state.isActive,
                    onClick = state.onManualWashClick
                )

                BookingButtonCottonWash(
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    enabled = !state.isActive,
                    onClick = state.onCottonWashClick
                )
            }
        }
    }
}
@Composable
private fun MachineStatus(isActive: Boolean) {
    val statusText = if (isActive) "Занята" else "Свободна"
    val statusColor = if (isActive) Color.Red else Color.Green

    Text(
        text = statusText,
        color = statusColor,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun WashingMachineNumber(modifier: Modifier = Modifier, number: Int) {
    Text(
        modifier = modifier.padding(bottom=4.dp),
        text = "Машина $number",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )

}

@Composable
private fun WashingMachineImage(modifier: Modifier = Modifier,isActive: Boolean=false) {
    Image(
        modifier = modifier,

        painter = painterResource(id = com.example.orderlist.data.R.drawable.bell_icon),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun BookingButtonFastWash(modifier: Modifier,enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = "Быстрая стирка")
    }
}

@Composable
private fun BookingButtonManualWash(modifier: Modifier,enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = "Ручная стирка")
    }
}

@Composable
private fun BookingButtonCottonWash(modifier: Modifier,enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = "Хлопок")
    }
}


