package spring.context.bean.dynamic.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.util.Collection;
import java.util.Map;

@Configuration
public class ContextImpl implements ApplicationContextAware, IContext {

    private static ApplicationContext applicationContext;

    private static Repositories repositories;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.repositories = new Repositories(applicationContext);
    }

    @Override
    public void createBean(final Class domainClass) {
        try {
            final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
            final DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;

            if (!beanFactory.containsBean(domainClass.getSimpleName())) {
                final GenericBeanDefinition generatorBean = new GenericBeanDefinition();
                generatorBean.setBeanClass(domainClass);
                generatorBean.setLazyInit(true);

                defaultListableBeanFactory.registerBeanDefinition(domainClass.getSimpleName(), generatorBean);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void destroyBean(final Class domainClass) {
        try {
            final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
            final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

            if (beanFactory.containsBean(domainClass.getSimpleName())) {
                beanDefinitionRegistry.removeBeanDefinition(domainClass.getSimpleName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <T> T getBean(final Class<T> domainClass) {
        try {
            return applicationContext.getAutowireCapableBeanFactory().getBean(domainClass);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <T> Collection<T> getBeansInterface(final Class<T> domainClassInterface) {
        try {
            if (domainClassInterface.isInterface()) {
                final Map<String, T> beansOfType = applicationContext.getBeansOfType(domainClassInterface);

                if (beansOfType != null) {
                    return beansOfType.values();
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean hasRepositoryFor(final Class<?> domainClass) {
        return repositories.hasRepositoryFor(domainClass);
    }

    @Override
    public <T, ID extends Object> JpaRepository<T, ID> getRepositoryFromClass(final Class<T> domainClass) {
        return (JpaRepository<T, ID>) repositories.getRepositoryFor(domainClass)
                .orElseThrow(() -> new RuntimeException("JpaRepository not found " + domainClass.getSimpleName()));
    }
}
