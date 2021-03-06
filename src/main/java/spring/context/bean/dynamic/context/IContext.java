package spring.context.bean.dynamic.context;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface IContext {

    IContext context = new ContextImpl();

    void createBean(Class domainClass);

    void destroyBean(Class domainClass);

    <T> T getBean(Class<T> domainClass);

    <T> Collection<T> getBeansInterface(Class<T> domainClassInterface);

    boolean hasRepositoryFor(Class<?> domainClass);

    <T, ID extends Object> JpaRepository<T, ID> getRepositoryFromClass(Class<T> domainClass);

    static IContext context() {
        return context;
    }

}
