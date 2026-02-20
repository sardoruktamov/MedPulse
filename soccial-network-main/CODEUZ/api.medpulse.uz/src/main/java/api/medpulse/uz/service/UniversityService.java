package api.medpulse.uz.service;

import api.medpulse.uz.entity.UniversityEntity;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository repository;

    // 1. CREATE (Admin/SuperAdmin)
    public UniversityEntity create(String name) {
        if (repository.existsByName(name)) {
            throw new AppBadException("Bu OTM allaqachon mavjud");
        }
        UniversityEntity entity = new UniversityEntity();
        entity.setName(name);
        return repository.save(entity);
    }

    // 2. GET LIST (Public - Dropdown uchun)
    public List<UniversityEntity> getList() {
        return repository.findAllByActiveTrue(Sort.by(Sort.Direction.ASC, "name"));
    }

    // 3. UPDATE (Admin)
    public UniversityEntity update(Integer id, String name) {
        UniversityEntity entity = repository.findById(id)
                .orElseThrow(() -> new AppBadException("OTM topilmadi"));
        entity.setName(name);
        return repository.save(entity);
    }

    // 4. DELETE (Admin - Soft delete tavsiya qilinadi)
    public void delete(Integer id) {
        UniversityEntity entity = repository.findById(id)
                .orElseThrow(() -> new AppBadException("OTM topilmadi"));
        entity.setActive(false); // Bazadan o'chirmaymiz, shunchaki yashiramiz
        repository.save(entity);
    }
}