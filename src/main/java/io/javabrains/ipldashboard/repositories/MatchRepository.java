package io.javabrains.ipldashboard.repositories;

import io.javabrains.ipldashboard.model.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> getByTeam1OrTeam2OrderByDateDesc(String team1, String team2, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE (m.team1 = :teamName OR m.team2 = :teamName) AND m.date BETWEEN :startDate AND :endDate ORDER BY m.date DESC")
    List<Match> getMatchesByTeamBetweenDates(
            @Param("teamName") String teamName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    default List<Match> findLatestMatchesByTeam(String teamName, int elementsPerPage) {
        return this.getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, elementsPerPage));
    }
}
