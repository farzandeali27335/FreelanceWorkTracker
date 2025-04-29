package tesside.farzandeali.freelanceworktracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tesside.farzandeali.freelanceworktracker.ui.theme.FreelanceWorkTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreelanceStartScreen()
        }
    }
}

@Composable
fun FreelanceStartScreen() {

    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        delay(3000)

        if(FreelancerPrefs.isUserSessionActive(context))
        {
            context.startActivity(Intent(context, HomeScreenActivity::class.java))
            context.finish()
        }else{
            context.startActivity(Intent(context, SignInActivity::class.java))
            context.finish()
        }

    }

    WelComeScreenDesign()
}


@Composable
fun WelComeScreenDesign() {
    val context = LocalContext.current

    // Animations
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        textOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, delayMillis = 300)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo with Zoom Animation
            Image(
                painter = painterResource(id = R.drawable.freelancer_appicon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 'By' Text with Slide and Fade Animation
            Text(
                text = "By",
                color = Color.Gray,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 'Farzand' Text with Slide and Fade Animation
            Text(
                text = "Farzand",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )
        }
    }
}
