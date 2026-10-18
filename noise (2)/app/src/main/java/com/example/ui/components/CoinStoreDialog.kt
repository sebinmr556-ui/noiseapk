package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun CoinStoreDialog(
    currentCoins: Int,
    onDismiss: () -> Unit,
    onPurchaseCoins: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(CoinGold.copy(alpha = 0.6f), DarkBorder))
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CoinGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "¢", color = ObsidianBlack, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Text(
                            text = "Get Noise Coins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextWhite
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Text(
                    text = "Current Balance: $currentCoins Coins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CoinGold
                )

                // Coin packs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 100 Coins
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(CoinGold.copy(alpha = 0.4f), DarkBorder))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onPurchaseCoins(100)
                                onDismiss()
                            }
                            .testTag("buy_100_coins_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "100", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CoinGold)
                            Text(text = "Coins", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = CoinGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$0.99",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoinGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // 550 Coins (Popular)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(NeonRose, ElectricPurple))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onPurchaseCoins(550)
                                onDismiss()
                            }
                            .testTag("buy_550_coins_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "550", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CoinGold)
                            Text(text = "Coins (+50 free)", fontSize = 10.sp, color = NeonRose, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = NeonRose
                            ) {
                                Text(
                                    text = "$4.99",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
