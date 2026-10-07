package com.example.appproject.ui.screens

import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.CoreValue
import com.example.appproject.data.model.Feature
import com.example.appproject.data.model.ManagementMember
import androidx.compose.ui.text.font.FontFamily

@Composable
fun AboutUsScreen() {
    val context = LocalContext.current
    val whatsappUrl = "https://api.whatsapp.com/send/?phone=628133315225&text=Hello+PT+ZUHANTO+TRAVEL+INDONESIA%2C+I+would+like+to+inquire+about+tour+packages+and+date+availability.+Thank+you.&type=phone_number&app_absent=0"
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val coreValues = listOf(
        CoreValue(stringResource(R.string.integrity), stringResource(R.string.integrity_desc)),
        CoreValue(
            stringResource(R.string.customer_satisfaction),
            stringResource(R.string.customer_satisfaction_desc)
        ),
        CoreValue(stringResource(R.string.innovation), stringResource(R.string.innovation_desc)),
        CoreValue(
            stringResource(R.string.professionalism),
            stringResource(R.string.professionalism_desc)
        ),
        CoreValue(
            stringResource(R.string.sustainable_growth),
            stringResource(R.string.sustainable_growth_desc)
        )
    )

    val managementTeam = listOf(
        ManagementMember("Dr. Moch. Zuhan", stringResource(R.string.role_chairman)),
        ManagementMember("Sumanto", stringResource(R.string.role_ceo)),
        ManagementMember("Hengky Melkior Sopaheluwaken", stringResource(R.string.role_cmo)),
        ManagementMember("Muhammad Sholihuddin", stringResource(R.string.role_coo)),
        ManagementMember("Riski Diah Meylani", stringResource(R.string.role_marketing_exec)),
        ManagementMember("Ria Ayu Nova Aprillia", stringResource(R.string.role_admin_officer))
    )

    val whyChooseUs = listOf(
        Feature(
            stringResource(R.string.prof_expertise_experience),
            stringResource(R.string.prof_expertise_experience_desc),
            Icons.Default.WorkspacePremium
        ),
        Feature(
            stringResource(R.string.pricing_transparency),
            stringResource(R.string.pricing_transparency_desc),
            Icons.Default.AttachMoney
        ),
        Feature(
            stringResource(R.string.customer_support_24_7),
            stringResource(R.string.customer_support_desc),
            Icons.Default.SupportAgent
        ),
        Feature(
            stringResource(R.string.safety_commitment),
            stringResource(R.string.safety_commitment_desc),
            Icons.Default.Security
        ),
        Feature(
            stringResource(R.string.customized_solutions),
            stringResource(R.string.customized_solutions_desc),
            Icons.Default.Tune
        ),
        Feature(
            stringResource(R.string.trusted_by_corporate),
            stringResource(R.string.trusted_by_corporate_desc),
            Icons.Default.Business
        )
    )

    val serviceGuarantee = listOf(
        Feature(
            stringResource(R.string.quality_assurance),
            stringResource(R.string.quality_assurance_desc),
            Icons.Default.CheckCircle
        ),
        Feature(
            stringResource(R.string.full_transparency),
            stringResource(R.string.full_transparency_desc),
            Icons.Default.CheckCircle
        ),
        Feature(
            stringResource(R.string.prof_service_standard),
            stringResource(R.string.prof_service_standard_desc),
            Icons.Default.CheckCircle
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Phone, contentDescription = null) },
                text = { Text(text = stringResource(R.string.contact_us), fontWeight = FontWeight.Bold, maxLines = 2) }
            )
        }
    ) { innerPadding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(600, easing = EaseOutQuad)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    AsyncImage(
                        model = R.drawable.backgroudmountain,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.7f
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.size(100.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AsyncImage(
                                model =  R.drawable.zti_logo,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.about_company),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.company_slogan),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    offset = PointF(2f, 2f).let { Offset(it.x, it.y) },
                                    blurRadius = 4f
                                )
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.company_description_short),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.company_description_full),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MissionVisionCard(
                        title = stringResource(R.string.our_mission),
                        description = stringResource(R.string.mission_description),
                        modifier = Modifier.weight(1f)
                    )
                    MissionVisionCard(
                        title = stringResource(R.string.our_vision),
                        description = stringResource(R.string.vision_description),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                SectionHeader(
                    title = stringResource(R.string.management_team),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                managementTeam.forEach { member ->
                    ManagementMemberItem(member = member)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(vertical = 40.dp, horizontal = 24.dp)
                ) {
                    SectionHeader(
                        title = stringResource(R.string.core_values),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    coreValues.forEach { value ->
                        CoreValueItem(value = value)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                SectionHeader(
                    title = stringResource(R.string.why_choose_us),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                whyChooseUs.forEach { feature ->
                    FeatureItem(feature = feature)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                SectionHeader(
                    title = stringResource(R.string.service_guarantee),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                serviceGuarantee.forEach { feature ->
                    FeatureItem(feature = feature)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight = FontWeight.Bold,
    style: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            style = style,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.secondary)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ManagementMemberItem(member: ManagementMember) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = member.role,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FeatureItem(feature: Feature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MissionVisionCard(title: String, description: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (title.contains("Mission")) Icons.Default.TrackChanges else Icons.Default.Visibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CoreValueItem(value: CoreValue) {
    val icon = when (value.title) {
        stringResource(R.string.integrity) -> Icons.Default.VerifiedUser
        stringResource(R.string.customer_satisfaction) -> Icons.Default.SentimentVerySatisfied
        stringResource(R.string.innovation) -> Icons.Default.Lightbulb
        stringResource(R.string.professionalism) -> Icons.Default.WorkspacePremium
        stringResource(R.string.sustainable_growth) -> Icons.Default.TrendingUp
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = value.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = value.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}