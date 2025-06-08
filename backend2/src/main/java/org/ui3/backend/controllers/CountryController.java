package org.ui3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.ui3.backend.models.Countries;
import org.ui3.backend.repositories.CountryRepository;
import org.ui3.backend.tools.DataValidationException;


import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/api/v1")
public class CountryController {

    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/countries")
    public Page<Countries> getAllCountries(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
        return countryRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }
    @GetMapping("/allcountries")
    public List<Countries> getAllCountriesNoPagination() {
        // Просто возвращает все страны без пагинации
        return countryRepository.findAll();
    }
    @GetMapping("/countries/{id}")
    public ResponseEntity<Countries> getCountry(@PathVariable(value = "id") Long countryId)
            throws DataValidationException {
        Countries country = countryRepository.findById(countryId)
                .orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
        return ResponseEntity.ok(country);
    }

    @PostMapping("/countries")
    public ResponseEntity<Object> createCountryPost(@RequestBody Map<String, String> body)
            throws DataValidationException {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new DataValidationException("Имя страны обязательно");
        }

        try {
            Countries country = new Countries();
            country.setName(name.trim());
            Countries nc = countryRepository.save(country);
            return new ResponseEntity<>(nc, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Countries> updateCountryPost(
            @PathVariable(value = "id") Long countryId,
            @RequestBody Map<String, String> body)
            throws DataValidationException {

        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new DataValidationException("Имя страны обязательно");
        }

        try {
            Countries country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));

            country.setName(name.trim());
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        } catch (Exception ex) {
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/countries/deletecountries")
    public ResponseEntity<?> deleteCountries(@Valid @RequestBody List<Countries> countries) {
        countryRepository.deleteAll(countries);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @DeleteMapping("/country/delete-by-id")
    public ResponseEntity<?> deleteCountriesById(@RequestParam("ids") List<Long> ids) {
        try {
            // Загружаем все страны по ID
            List<Countries> countries = new ArrayList<>();
            for (Long id : ids) {
                countryRepository.findById(id).ifPresent(countries::add);
            }

            // Удаляем загруженные страны
            if (!countries.isEmpty()) {
                countryRepository.deleteAll(countries);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception ex) {
            // Логирование ошибки для диагностики
            ex.printStackTrace();
            return new ResponseEntity<>(
                    Map.of("error", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}