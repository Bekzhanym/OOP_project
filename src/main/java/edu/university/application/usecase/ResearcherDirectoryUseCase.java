package edu.university.application.usecase;

import edu.university.domain.model.Researcher;
import edu.university.domain.model.User;
import edu.university.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ResearcherDirectoryUseCase {
    private final UserRepository userRepository;

    public ResearcherDirectoryUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Researcher> findAllResearchers() {
        List<Researcher> researchers = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            if (user instanceof Researcher researcher) {
                researchers.add(researcher);
            }
        }
        return researchers;
    }

    public Optional<Researcher> findTopByHIndex() {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(Researcher::getHIndex));
    }
}
