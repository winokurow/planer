import models.WeightObject
import java.time.LocalTime

data class Event(val name: String, val beginTime: LocalTime, val endTime: LocalTime, val weights: List<WeightObject>)