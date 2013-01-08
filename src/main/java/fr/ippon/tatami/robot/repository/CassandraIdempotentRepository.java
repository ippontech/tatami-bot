package fr.ippon.tatami.robot.repository;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Used to de-deplucate messages.
 */
@Component
public class CassandraIdempotentRepository implements IdempotentRepository<String> {

    private final Log log = LogFactory.getLog(CassandraIdempotentRepository.class);

    private final static String KEY = "Default";

    private final static String TATAMIBOT_DUPLICATE_CF = "TatamiBotDuplicate";

    private final static int COLUMN_TTL = 60 * 60 * 24 * 30; // The column is stored for 30 days.

    @Inject
    private Keyspace keyspaceOperator;

    @Override
    public boolean add(String name) {
        if (contains(name)) {
            log.debug("Duplicate message detected!");
            return false;
        } else {
            log.debug("Adding new message to the idempotent repository");
            HColumn<String, String> column =
                    HFactory.createColumn(
                            name,
                            "",
                            COLUMN_TTL,
                            StringSerializer.get(),
                            StringSerializer.get());

            Mutator<String> mutator =
                    HFactory.createMutator(keyspaceOperator, StringSerializer.get());

            mutator.insert(KEY, TATAMIBOT_DUPLICATE_CF, column);
            return true;
        }
    }

    @Override
    public boolean contains(String name) {
        log.debug("Testing message duplication");
        ColumnQuery<String, String, String> query = HFactory.createStringColumnQuery(keyspaceOperator);

        HColumn<String, String> column =
                query.setColumnFamily(TATAMIBOT_DUPLICATE_CF)
                        .setKey(KEY)
                        .setName(name)
                        .execute()
                        .get();

        if (column == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean remove(String name) {
        Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
        mutator.delete(KEY, TATAMIBOT_DUPLICATE_CF, name, StringSerializer.get());
        return true;
    }

    @Override
    public boolean confirm(String key) {
        return true; // noop
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
    }
}
