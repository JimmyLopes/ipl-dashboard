package io.javabrains.ipldashboard.configuration.listener;


import io.javabrains.ipldashboard.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager em;

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Map<String, Team> teamData = new HashMap<>();

            em.createQuery("SELECT distinct m.team1, count(*) from Match m group by m.team1", Object[].class)
                    .getResultList()
                    .stream()
                    .map(res -> new Team((String) res[0], (long) res[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));

            for (Object[] res : em.createQuery("SELECT distinct m.team2, count(*) from Match m group by m.team2", Object[].class)
                    .getResultList()) {
                Team team = teamData.get(res[0]);
                team.setTotalMatches(team.getTotalMatches() + (long) res[1]);
            }

            for (Object[] res : em.createQuery("SELECT m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                    .getResultList()) {
                Team team = teamData.get(res[0]);
                if (Objects.nonNull(team)) team.setTotalWins(team.getTotalMatches() + (long) res[1]);
            }

            teamData.values().forEach(em::persist);
        }
    }
}