package it.devchallenge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import it.devchallenge.api.repository.DeputyRepository;
import it.devchallenge.api.repository.ProjectRepository;
import it.devchallenge.graph.domain.Project;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfVotingApplicationTests {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DeputyRepository deputyRepository;

    @Test
    public void findAll() throws Exception {
        List<Project> projects = projectRepository.findAll(new PageRequest(0, 100)).getContent();
        assertThat(projects, hasSize(6));
        assertThat(projects, hasItem(allOf(
                hasProperty("name", equalTo("Про затвердження порядку денного")),
                hasProperty("number", equalTo("бн  За основу")),
                hasProperty("accepted", equalTo(true))
        )));
        assertThat(projects, hasItem(allOf(
                hasProperty("name", equalTo("Про затвердження порядку денного")),
                hasProperty("number", equalTo("бн  В цілому")),
                hasProperty("accepted", equalTo(true))
        )));
    }

    @Test
    public void findOne() throws Exception {
        List<Project> projects = projectRepository.findAll(new PageRequest(0, 100)).getContent();
        assertThat(projects, hasSize(6));
        Long id = projects.get(0).getId();
        Project project = projectRepository.findOne(id);
        assertThat(project, notNullValue());
        assertThat(project.getName(), notNullValue());
        assertThat(project.getNumber(), notNullValue());
        assertThat(project.getSession(), notNullValue());
        assertThat(project.getVotes(), notNullValue());
    }

    @Test
    public void findByName() throws Exception {
        List<Project> projects = projectRepository
                .findByNameLike("Про затвердження порядку*", new PageRequest(0, 100))
                .getContent();
        assertThat(projects, hasSize(2));
        assertThat(projects, hasItem(allOf(
                hasProperty("name", equalTo("Про затвердження порядку денного")),
                hasProperty("number", equalTo("бн  За основу")),
                hasProperty("accepted", equalTo(true))
        )));
        assertThat(projects, hasItem(allOf(
                hasProperty("name", equalTo("Про затвердження порядку денного")),
                hasProperty("number", equalTo("бн  В цілому")),
                hasProperty("accepted", equalTo(true))
        )));
    }

    @Test
    public void findBySessionName() throws Exception {
        List<Project> projects = projectRepository
                .findBySessionNameLike("21 позачергова сесія*", new PageRequest(0, 100))
                .getContent();
        assertThat(projects, hasSize(6));
    }

    @Test
    public void findMostClosestDeputies() throws Exception {
        List<Map<String, Object>> closestDeputies = deputyRepository.findClosestDeputies(3);
        assertThat(closestDeputies, hasSize(3));

        Map<String, Object> item1 = new HashMap<>();
        item1.put("first", "Тютюнник Андрій Анатолійович");
        item1.put("second", "Коваленко Вікторія Миколаївна");
        item1.put("sameVotes", 6L);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("first", "Здоровець Олексій Михайлович");
        item2.put("second", "Гредунов Євгеній Валерійович");
        item2.put("sameVotes", 6L);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("first", "Тоцька Тетяна Петрівна");
        item3.put("second", "Бабич Петро Іванович");
        item3.put("sameVotes", 6L);

        assertThat(closestDeputies, hasItems(item1, item2, item3));
    }

    @Test
    public void findClosestToDeputy() throws Exception {
        List<Map<String, Object>> closestDeputies = deputyRepository.findClosestToDeputy("Тютюнник Андрій Анатолійович", 2);
        assertThat(closestDeputies, hasSize(2));

        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Темченко Людмила Григорівна");
        item1.put("sameVotes", 6L);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Сапожко Ігор Васильович");
        item2.put("sameVotes", 6L);

        assertThat(closestDeputies, hasItems(item1, item2));
    }

    @Test
    public void findFarthestToDeputy() throws Exception {
        List<Map<String, Object>> farthestDeputies = deputyRepository.findClosestToDeputy("Гредунов Євгеній Валерійович", 2);
        assertThat(farthestDeputies, hasSize(2));

        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Василенко Андрій Петрович");
        item1.put("sameVotes", 6L);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Бойко Сергій Олександрович");
        item2.put("sameVotes", 4L);

        assertThat(farthestDeputies, hasItems(item1, item2));
    }
}
