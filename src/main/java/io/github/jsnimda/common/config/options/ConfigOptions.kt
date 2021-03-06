package io.github.jsnimda.common.config.options

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.ConfigOptionNumericBase
import io.github.jsnimda.common.config.IConfigOptionPrimitive
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.gui.widgets.ConfigButtonInfo
import io.github.jsnimda.common.util.next
import io.github.jsnimda.common.util.previous

class ConfigDouble(defaultValue: Double, minValue: Double, maxValue: Double) :
  ConfigOptionNumericBase<Double>(defaultValue, minValue, maxValue) {
  override fun setNumericValue(value: Number) = run { this.value = value.toDouble() }
  val doubleValue
    get() = value
}

class ConfigInteger(defaultValue: Int, minValue: Int, maxValue: Int) :
  ConfigOptionNumericBase<Int>(defaultValue, minValue, maxValue) {
  override fun setNumericValue(value: Number) = run { this.value = value.toInt() }
  val integerValue
    get() = value
}

open class ConfigBoolean(final override val defaultValue: Boolean) :
  ConfigOptionBase(), IConfigOptionPrimitive<Boolean>, IConfigOptionToggleable {
  override var value = defaultValue
  override fun toggleNext() = run { value = !value }
  override fun togglePrevious() = run { value = !value }
  val booleanValue
    get() = value
}

class ConfigEnum<E : Enum<E>>(override val defaultValue: E) :
  ConfigOptionBase(), IConfigOptionPrimitive<E>, IConfigOptionToggleable {
  override var value = defaultValue
  override fun toggleNext() = run { value = value.next() }
  override fun togglePrevious() = run { value = value.previous() }
}

class ConfigString(override val defaultValue: String) :
  ConfigOptionBase(), IConfigOptionPrimitive<String> {
  override var value = defaultValue
}

class ConfigButton(val info: ConfigButtonInfo) : ConfigOptionBase() { // fake config that acts as button
  override fun toJsonElement(): JsonElement {
    Log.error("this is a config button") // shouldn't be called
    return JsonNull.INSTANCE
  }

  override fun fromJsonElement(element: JsonElement) {
    Log.warn("this is a config button $element")
  }

  override val isModified = false
  override fun resetToDefault() {}
}