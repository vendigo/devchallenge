package it.devchallenge.api;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

import it.devchallenge.api.repository.ProjectRepository;

@Component
public class SpringDataRestConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.withEntityLookup()
                .forRepository(ProjectRepository.class, (p)->p.getId().toString(), (repo, id)->repo.findOne(Long.parseLong(id), 2));
    }
}
