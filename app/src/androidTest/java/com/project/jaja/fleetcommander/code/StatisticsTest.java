package com.project.jaja.fleetcommander.code;

import com.project.jaja.fleetcommander.Statistic;
import com.project.jaja.fleetcommander.Statistics;

import junit.framework.TestCase;

import java.util.ArrayList;

public class StatisticsTest extends TestCase {

    public void testAll() throws Exception {
        String json = "{\"db:48:59:d2:88:87\":[{\"myScore\":55,\"opponentScore\":40,\"dateTime\":\"17201122011\"},{\"myScore\":15,\"opponentScore\":35,\"dateTime\":\"154927102010\"},{\"myScore\":25,\"opponentScore\":15,\"dateTime\":\"211418102012\"}],\"68:a7:42:1b:da:a2\":[{\"myScore\":80,\"opponentScore\":100,\"dateTime\":\"20292662013\"},{\"myScore\":40,\"opponentScore\":45,\"dateTime\":\"18102362010\"},{\"myScore\":0,\"opponentScore\":60,\"dateTime\":\"12171352014\"}],\"14:1a:6b:f9:ef:d1\":[{\"myScore\":95,\"opponentScore\":65,\"dateTime\":\"112310112013\"},{\"myScore\":65,\"opponentScore\":80,\"dateTime\":\"16176122010\"},{\"myScore\":35,\"opponentScore\":45,\"dateTime\":\"13481912013\"}],\"e5:1e:1e:ac:b9:5f\":[{\"myScore\":0,\"opponentScore\":60,\"dateTime\":\"23152252012\"},{\"myScore\":65,\"opponentScore\":95,\"dateTime\":\"17522532012\"},{\"myScore\":5,\"opponentScore\":45,\"dateTime\":\"2033662010\"}],\"b8:9d:fd:dd:83:c5\":[{\"myScore\":20,\"opponentScore\":100,\"dateTime\":\"16271712013\"},{\"myScore\":80,\"opponentScore\":5,\"dateTime\":\"12412332014\"},{\"myScore\":65,\"opponentScore\":40,\"dateTime\":\"22182252010\"}],\"25:15:ab:b8:3c:24\":[{\"myScore\":100,\"opponentScore\":80,\"dateTime\":\"203725112014\"},{\"myScore\":50,\"opponentScore\":40,\"dateTime\":\"12492812011\"},{\"myScore\":35,\"opponentScore\":35,\"dateTime\":\"175417122010\"}],\"e6:0b:5c:61:bd:82\":[{\"myScore\":80,\"opponentScore\":50,\"dateTime\":\"11172382012\"},{\"myScore\":95,\"opponentScore\":50,\"dateTime\":\"11304122014\"},{\"myScore\":85,\"opponentScore\":25,\"dateTime\":\"10392712013\"}],\"03:99:11:a3:b8:ff\":[{\"myScore\":5,\"opponentScore\":75,\"dateTime\":\"1933232013\"},{\"myScore\":45,\"opponentScore\":35,\"dateTime\":\"1458152012\"},{\"myScore\":25,\"opponentScore\":20,\"dateTime\":\"13122372012\"}],\"46:30:56:39:fe:5d\":[{\"myScore\":0,\"opponentScore\":95,\"dateTime\":\"144326122011\"},{\"myScore\":55,\"opponentScore\":90,\"dateTime\":\"13551972013\"},{\"myScore\":10,\"opponentScore\":45,\"dateTime\":\"1051622014\"}],\"9c:69:dc:22:fd:31\":[{\"myScore\":5,\"opponentScore\":75,\"dateTime\":\"23236122010\"},{\"myScore\":85,\"opponentScore\":85,\"dateTime\":\"14122182010\"},{\"myScore\":0,\"opponentScore\":15,\"dateTime\":\"1521432010\"}]}";
        Statistics stats = new Statistics(json);

        ArrayList<Statistic> testList = new ArrayList<Statistic>();
        Statistic stat1 = new Statistic(55, 40, "17201122011");
        Statistic stat2 = new Statistic(15, 35, "154927102010");
        Statistic stat3 = new Statistic(25, 15, "211418102012");

        testList.add(stat1);
        testList.add(stat2);
        testList.add(stat3);

        assertEquals(stats.getPlayerStatistics("db:48:59:d2:88:87"), testList);
    }
}