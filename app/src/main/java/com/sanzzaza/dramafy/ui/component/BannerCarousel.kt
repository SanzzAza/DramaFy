package com.sanzzaza.dramafy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sanzzaza.dramafy.data.model.BannerDto
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
    banners: List<BannerDto>,
    onBannerClick: (BannerDto) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState, banners) {
        if (banners.size > 1) {
            while (true) {
                delay(4500)
                val next = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Box(modifier = modifier.padding(contentPadding)) {
        HorizontalPager(state = pagerState, pageSpacing = 12.dp) { page ->
            val banner = banners[page]
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                onClick = { onBannerClick(banner) }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (banner.image.isNotBlank()) {
                        AsyncImage(
                            model = banner.image,
                            contentDescription = banner.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f)
                                    ),
                                    startY = 60f
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        if (banner.title.isNotBlank()) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
