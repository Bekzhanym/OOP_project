package kbtu_oop_project.application.usecase;

import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.repository.UserRepository;

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

    public Optional<Researcher> findTopResearcherByTotalCitations() {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(ResearcherDirectoryUseCase::totalCitations));
    }

    public Optional<Researcher> findTopResearcherByCitationsInYear(int year) {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(r -> citationsInYear(r, year)));
    }

    public void printAllResearchersPapersSorted(Comparator<ResearchPaper> comparator) {
        for (Researcher researcher : findAllResearchers()) {
            System.out.println("=== Papers (" + researcher.getClass().getSimpleName()
                    + ", h-index=" + researcher.getHIndex() + ") ===");
            researcher.printPapers(comparator);
        }
    }

    private static int totalCitations(Researcher researcher) {
        return researcher.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum();
    }

    private static int citationsInYear(Researcher researcher, int year) {
        return researcher.getPapers().stream()
                .filter(p -> p.getDate() != null && p.getDate().getYear() == year)
                .mapToInt(ResearchPaper::getCitations)
                .sum();
    }
}
