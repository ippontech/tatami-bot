package fr.ippon.tatami.robot.config;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cassandra configuration file.
 *
 * @author Julien Dubois
 */
@Configuration
public class CassandraConfiguration {

    private final Log log = LogFactory.getLog(CassandraConfiguration.class);

    @Value("${cassandra.host}")
    private String cassandraHost;

    @Value("${cassandra.clusterName}")
    private String cassandraClusterName;

    @Value("${cassandra.keyspace}")
    private String cassandraKeyspace;

    @Bean
    public Keyspace keyspaceOperator() {
        CassandraHostConfigurator cassandraHostConfigurator = new CassandraHostConfigurator(cassandraHost);
        ThriftCluster cluster = new ThriftCluster(cassandraClusterName, cassandraHostConfigurator);
        ConfigurableConsistencyLevel consistencyLevelPolicy = new ConfigurableConsistencyLevel();
        consistencyLevelPolicy.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);

        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(cassandraKeyspace);
        if (keyspaceDef == null) {
            log.fatal("Keyspace \"" + cassandraKeyspace + "\" does not exist, this is a fatal error!");
            new RuntimeException("Keyspace " + cassandraKeyspace + "does not exist");
        }
        return HFactory.createKeyspace(cassandraKeyspace, cluster, consistencyLevelPolicy);
    }
}
