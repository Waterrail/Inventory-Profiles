package io.github.jsnimda.inventoryprofiles.debug

import io.github.jsnimda.common.gui.widgets.ButtonWidget
import io.github.jsnimda.common.gui.widgets.ConfigButtonInfo
import io.github.jsnimda.common.util.div
import io.github.jsnimda.common.util.writeToFile
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.alias.Item
import io.github.jsnimda.common.vanilla.alias.Registry
import io.github.jsnimda.inventoryprofiles.event.TellPlayer
import io.github.jsnimda.inventoryprofiles.ingame.`(getIdentifier)`
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagCollection

// ============
// vanillamapping code depends on mappings
// ============

object GenerateTagVanillaTxtButtonInfo : ConfigButtonInfo() {
  val fileDatapack = VanillaUtil.configDirectory("inventoryprofiles") / "tags.vanilla.datapack.txt"
  val fileHardcoded = VanillaUtil.configDirectory("inventoryprofiles") / "tags.vanilla.hardcoded.txt"

  override val buttonText: String
    get() = "generate tags.vanilla.txt"

  override fun onClick(widget: ButtonWidget) {
    TellPlayer.chat("Generate tags.vanilla.txt")
    ItemTags.getCollection().toTagTxtContent().writeToFile(fileHardcoded)
    val server = Vanilla.server()
    server ?: return Unit.also { TellPlayer.chat("Not integrated server!!!") }
    server.networkTagManager.items.toTagTxtContent().writeToFile(fileDatapack) // tagtagManager.items() = forge networkTagManager.items
  } // eventually they are the same ~.~

  val Identifier.omittedString: String // omit minecraft
    get() = if (namespace == "minecraft") path else toString()

  val String.omittedString: String // omit minecraft
    get() = removePrefix("minecraft:")

  fun TagCollection<Item>.toTagTxtContent(): String { // lets sort it
    val list = mutableListOf<Pair<String, MutableList<String>>>()
    for ((identifier, tag) in tagMap) { // forge tagMap = entries
      list += identifier.toString() to tag.allElements.map { Registry.ITEM.`(getIdentifier)`(it).toString() }.toMutableList() // allElements = values
    }
    list.sortBy { it.first }
    list.forEach { it.second.sort() }
    val omittedList = list.map { (a, b) -> a.omittedString to b.map { it.omittedString } }
    return omittedList.flatMap { (a, b) ->
      listOf("#$a") + b.map { "    $it" } + listOf("")
    }.joinToString("\n")
  }

}