package github.barebones.wisespend.ui.components

import android.graphics.Matrix
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

enum class MorphShapeType {
    STAR_6,
    SQUIRCLE,
    SCALLOPED_8
}

class SingleMorphShape(
    private val polygon: RoundedPolygon,
    private val rotation: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (size.minDimension <= 0f) {
            return Outline.Rectangle(Rect(0f, 0f, 0f, 0f))
        }

        val matrix = Matrix().apply {
            val scale = size.minDimension / 2f
            postScale(scale, scale)
            postRotate(rotation)
            postTranslate(size.width / 2f, size.height / 2f)
        }

        val path = polygon.toPath()
            .apply { transform(matrix) }
            .asComposePath()

        return Outline.Generic(path)
    }
}

@Composable
fun MorphingIcon(
    modifier: Modifier = Modifier,
    shapeType: MorphShapeType = MorphShapeType.SQUIRCLE,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector = Icons.Default.AddComment,
    iconSize: Dp = 48.dp,
    rotationDuration: Int = 8000
) {
    val shapes = remember {
        mapOf(
            MorphShapeType.STAR_6 to RoundedPolygon.star(
                numVerticesPerRadius = 6,
                radius = 1f,
                innerRadius = 0.75f,
                rounding = CornerRounding(0.5f)
            ),
            MorphShapeType.SQUIRCLE to RoundedPolygon(
                numVertices = 8,
                radius = 1f,
                rounding = CornerRounding(0.3f)
            ),
            MorphShapeType.SCALLOPED_8 to RoundedPolygon.star(
                numVerticesPerRadius = 8,
                radius = 1f,
                innerRadius = 0.85f,
                rounding = CornerRounding(1f)
            )
        )
    }

    val shape = shapes[shapeType]!!

    val infiniteTransition = rememberInfiniteTransition(label = "iconRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "iconRotation"
    )

    Box(
        modifier = modifier
            .clip(SingleMorphShape(shape, rotation))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
    }
}

@Preview(showBackground = true, name = "Star Shape")
@Composable
fun StarShapePreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        MorphingIcon(
            modifier = Modifier.size(140.dp),
            shapeType = MorphShapeType.STAR_6,
            backgroundColor = Color(0xFF6200EE),
            rotationDuration = 6000
        )
    }
}

@Preview(showBackground = true, name = "Squircle Shape")
@Composable
fun SquircleShapePreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        MorphingIcon(
            modifier = Modifier.size(140.dp),
            shapeType = MorphShapeType.SQUIRCLE,
            backgroundColor = Color(0xFF018786),
            rotationDuration = 10000
        )
    }
}
@Preview(showBackground = true, name = "Scalloped Shape")
@Composable
fun ScallopedShapePreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        MorphingIcon(
            modifier = Modifier.size(140.dp),
            shapeType = MorphShapeType.SCALLOPED_8,
            backgroundColor = Color(0xFF4CAF50),
            rotationDuration = 7000
        )
    }
}