package com.first.hotsauces

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/hotsauces")
class HotSauceController(private val hotSauceRepository: HotSauceRepository) {

    // GET api/hotsauces
    @GetMapping("")
    fun getAll(@RequestParam(value="brandname", required = false, defaultValue = "") brandNameFilter: String,
               @RequestParam(value="saucename", required = false, defaultValue = "") sauceNameFilter: String,
               @RequestParam(value="desc", required = false, defaultValue = "") descFilter: String,
               @RequestParam(value="minheat", required = false, defaultValue = "") minHeat: String,
               @RequestParam(value="maxheat", required = false, defaultValue = "") maxHeat: String): ResponseEntity<List<HotSauce>> {
        val MAX_SCOVILLE = 3_000_000 // At this point, it's no longer food, but a weapon
        val minHeatFilter = if (!minHeat.isNullOrBlank()) minHeat.toInt() else 0
        val maxHeatFilter = if (!maxHeat.isNullOrBlank()) maxHeat.toInt() else MAX_SCOVILLE
        return ResponseEntity(hotSauceRepository.findAll()
            .asSequence()
            .filter { it.brandName.contains(brandNameFilter, true) }
            .filter { it.sauceName.contains(sauceNameFilter, true) }
            .filter { it.description.contains(descFilter, true) }
            .filter { it.heat >= minHeatFilter }
            .filter { it.heat <= maxHeatFilter }
            .toList(),
            HttpStatus.OK
        )
    }

    // GET api/hotsauces/count
    @GetMapping("/count")
    fun getCount(): ResponseEntity<Long> = ResponseEntity(hotSauceRepository.count(),
            HttpStatus.OK)

    // GET api/hotsauces/{id}
    @GetMapping("/{id}")
    fun getHotSauce(@PathVariable id: Long): ResponseEntity<Optional<HotSauce>> {
        return if (hotSauceRepository.existsById(id)) {
            ResponseEntity(hotSauceRepository.findById(id), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // POST api/hotsauce
    @PostMapping()
    fun createHotSauce(@RequestBody hotSauce: HotSauce): ResponseEntity<HotSauce> {
        return ResponseEntity(hotSauceRepository.save(hotSauce), HttpStatus.CREATED)
    }

    // PUT api/hotsauces/{id}
    @PutMapping("/{id}")
    fun updateHotSauce(@PathVariable id: Long, @RequestBody sauceChanges: HotSauce): ResponseEntity<HotSauce?> {
        return if (hotSauceRepository.existsById(id)) {
            val originalSauce = hotSauceRepository.findById(id).get()
            val updatedSauce = HotSauce(
                id = id,
                brandName = if (sauceChanges.brandName != "") sauceChanges.brandName else originalSauce.brandName,
                sauceName = if (sauceChanges.sauceName != "") sauceChanges.sauceName else originalSauce.sauceName,
                description = if (sauceChanges.description != "") sauceChanges.description else originalSauce.description,
                url = if (sauceChanges.url != "") sauceChanges.url else originalSauce.url,
                heat = if (sauceChanges.heat != 0) sauceChanges.heat else originalSauce.heat
            )
            ResponseEntity(hotSauceRepository.save(updatedSauce), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // DELETE api/hotsauces/{id}
    @DeleteMapping("/{id}")
    fun deleteHotSauce(@PathVariable id: Long): ResponseEntity<HotSauce?> {
        return if (hotSauceRepository.existsById(id)) {
            hotSauceRepository.deleteById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

}
