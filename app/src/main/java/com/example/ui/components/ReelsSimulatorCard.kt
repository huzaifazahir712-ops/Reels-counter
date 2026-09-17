package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ColorTikTokCyan
import com.example.ui.theme.ColorTikTokPink

private data class MockReel(
    val author: String,
    val title: String,
    val tags: String,
    val likes: String,
    val bgColors: List<Color>
)

private val MOCK_REELS = listOf(
    MockReel(
        author = "@productivity_hacks",
        title = "3 morning habits that save 2 hours daily ☀️",
        tags = "#focus #habits #routine",
        likes = "84.2K",
        bgColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0284C7))
    ),
    MockReel(
        author = "@science_burst",
        title = "Why your brain craves short-form video dopamine 🧠",
        tags = "#neuroscience #psychology",
        likes = "142.6K",
        bgColors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF6366F1))
    ),
    MockReel(
        author = "@chef_artisan",
        title = "Crispy garlic butter sourdough in under 60 seconds 🍞",
        tags = "#cooking #recipes #foodtok",
        likes = "215.1K",
        bgColors = listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFFD97706))
    ),
    MockReel(
        author = "@nature_planet",
        title = "Crystal fjord waters in Northern Norway at midnight 🌊",
        tags = "#travel #norway #wanderlust",
        likes = "98.9K",
        bgColors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
    ),
    MockReel(
        author = "@tech_insider",
        title = "The invisible mechanical design inside everyday items ⚙️",
        tags = "#engineering #tech #design",
        likes = "57.3K",
        bgColors = listOf(Color(0xFF311042), Color(0xFF581C87), Color(0xFF9333EA))
    )
)

@Composable
fun ReelsSimulatorCard(
    onReelScrolled: () -> Unit,
    onBatchScrolled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentReelIndex by remember { mutableIntStateOf(0) }
    var scrollFeedbackCount by remember { mutableIntStateOf(0) }

    val currentReel = MOCK_REELS[currentReelIndex % MOCK_REELS.size]

    fun triggerScrollNext() {
        currentReelIndex++
        scrollFeedbackCount++
        onReelScrolled()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reels_simulator_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Interactive Reel Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Swipe up or tap to simulate TikTok scrolling",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColorTikTokPink.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "LIVE TEST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorTikTokPink,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Simulated Vertical Reel Screen with Swipe Gesture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .pointerInput(Unit) {
                        var totalDrag = 0f
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                totalDrag += dragAmount
                            },
                            onDragEnd = {
                                if (totalDrag < -40f) {
                                    // Swiped upwards!
                                    triggerScrollNext()
                                }
                                totalDrag = 0f
                            }
                        )
                    }
                    .testTag("mock_reel_viewport")
            ) {
                AnimatedContent(
                    targetState = currentReel,
                    transitionSpec = {
                        slideInVertically { height -> height } togetherWith
                                slideOutVertically { height -> -height }
                    },
                    label = "reelTransition"
                ) { reel ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Brush.verticalGradient(reel.bgColors))
                            .padding(16.dp)
                    ) {
                        // Play Icon centered subtly
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Right action bar (likes, share)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Likes",
                                    tint = ColorTikTokPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = reel.likes,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Bottom Reel Info
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth(0.75f)
                                .padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = reel.author,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = reel.title,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = ColorTikTokCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "original sound - trending audio",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Upward scroll hint indicator
                        Surface(
                            modifier = Modifier.align(Alignment.TopCenter),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = ColorTikTokCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Swipe Up or Tap Next",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Button(
                onClick = { triggerScrollNext() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("scroll_next_reel_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Scroll to Next Reel (+1)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Burst Buttons for testing productivity limits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { onBatchScrolled(5) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("burst_5_reels_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+5 Burst", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                FilledTonalButton(
                    onClick = { onBatchScrolled(15) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("burst_15_reels_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+15 Session", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                FilledTonalButton(
                    onClick = { onBatchScrolled(30) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("burst_30_reels_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+30 Heavy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
