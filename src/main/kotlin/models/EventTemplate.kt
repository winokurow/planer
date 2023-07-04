package models

import java.time.Duration

data class EventTemplate(
    var name: String,
    val weight: Int,
    var duration: Duration,
    val weights: MutableList<WeightObject>,
    val postProcessing: String
)