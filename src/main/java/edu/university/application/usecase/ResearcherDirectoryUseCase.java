package edu.university.application.usecase;

import edu.university.domain.model.ResearchPaper;
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

    /** Highest individual h-index (legacy / diagram-oriented metric). */
    public Optional<Researcher> findTopByHIndex() {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(Researcher::getHIndex));
    }

    /** Sum of citations across all papers (school-wide “top cited researcher”). */
    public Optional<Researcher> findTopResearcherByTotalCitations() {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(ResearcherDirectoryUseCase::totalCitations));
    }

    /** Restrict citation sum to papers published in the given calendar year. */
    public Optional<Researcher> findTopResearcherByCitationsInYear(int year) {
        return findAllResearchers().stream()
                .max(Comparator.comparingInt(r -> citationsInYear(r, year)));
    }

    /** Prints each researcher's papers sorted by the given comparator (assignment workflow). */
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
