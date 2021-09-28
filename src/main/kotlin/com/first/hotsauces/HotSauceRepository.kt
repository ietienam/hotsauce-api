package com.first.hotsauces

import org.springframework.data.repository.CrudRepository

interface HotSauceRepository: CrudRepository<HotSauce, Long>
