/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.utils;

import com.google.common.base.CharMatcher;
import org.easyrec.model.core.TenantVO;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.Session;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.io.Text;
import org.easyrec.utils.spring.cli.AbstractDependencyInjectionSpringCLI;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * @author pmarschik
 */
public class Movielens1MImporter extends AbstractDependencyInjectionSpringCLI {

    private static class Movie {

        private int id;
        private String name;
        private String genres;

        public Movie(int id, String name, String genres) {
            this.id = id;
            this.name = name;
            this.genres = genres;
        }

        public String getGenres() {
            return genres;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;
    private OperatorDAO operatorDAO;
    private ShopRecommenderService shopRecommenderService;
    private static final Logger logger = Logger.getLogger(Movielens1MImporter.class.getName());
    private static final double RATING_COUNT = 1000209;
    private static final double MOVIE_COUNT = 3952;
    private static final double USER_COUNT = 6040;

    public static void main(String[] args) {
        Movielens1MImporter importer = new Movielens1MImporter();
        importer.processCommandLineCall(args);
    }

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setShopRecommenderService(ShopRecommenderService shopRecommenderService) {
        this.shopRecommenderService = shopRecommenderService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"spring/web/importer/movielens/AllInOne_Movielens1M.xml"};
    }

    @Override
    protected void usage() {
        System.out.println("Usage: java -...ImporterCLI <path_to_movielens_1M>");
    }

    @Override
    protected int processCommandLineCall(String[] args) {
        try {
            processCommandLineCallEx(args);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processCommandLineCallEx(String[] args) throws Exception {
        String pathToDataset = "C:\\DATA\\datasets\\ml1M";

        if (args.length != 1) {
            if (!new File(pathToDataset).exists()) usage();
        } else pathToDataset = args[0];

        File datasetFile = new File(pathToDataset);
        File moviesFile = new File(datasetFile.getAbsolutePath() + File.separator + "movies.dat");
        File ratingsFile = new File(datasetFile.getAbsolutePath() + File.separator + "ratings.dat");

        if (!datasetFile.exists()) {
            System.err.println("Path \"" + pathToDataset + "\" doesn't exist.");
            return;
        }

        if (!moviesFile.exists() || !ratingsFile.exists()) {
            logger.info(moviesFile.toString());
            logger.info(ratingsFile.toString());
            System.err.println("movies.dat or ratings.dat not found.");
            return;
        }

        if (!operatorDAO.exists("movielens")) {
            String apiKey = Text.generateHash("movielens");
            operatorDAO.add("movielens", "movielens", "", "", "", "", "GroupLens", "", apiKey, "");
        }

        Operator operator = operatorDAO.get("movielens");

        if (remoteTenantDAO.exists("Movielens-1M")) {
            System.out.println("Tenant Movielens-1M already exists. SKIPPING import.");
            return;
        }

        String tenantDescription = "These files contain 1,000,209 anonymous ratings of approximately 3,900 movies made by 6,040 MovieLens users who joined MovieLens in 2000.";

        TenantVO tenant = new TenantVO("Movielens-1M", tenantDescription, 1, 5, 2.5);
        tenantService.insertTenantWithTypes(tenant, null);

        System.out.println("Tenant got id: " + tenant.getId());

        remoteTenantDAO.update(operator.getOperatorId(), tenant.getId(), "", tenantDescription);

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenant.getId());

        Map<Integer, Movie> movies = new TreeMap<Integer, Movie>();

        Scanner movieScanner = new Scanner(moviesFile);
        movieScanner.useDelimiter("::|(\\r)?\\n");

        System.out.println("Loading movies ...");

        int line = 0;
        int lastPerc = 0;

        do {
            line++;

            double percentage = line * 100.0 / MOVIE_COUNT;
            if (Math.floor(percentage) % 10 == 0 && (int) percentage != lastPerc) {
                lastPerc = (int) percentage;
                System.out.print(lastPerc + "% ");
            }

            int id = movieScanner.nextInt();
            String name = movieScanner.next();
            String genres = movieScanner.next();
            genres = CharMatcher.is('|').replaceFrom(genres, ", ");

            movies.put(id, new Movie(id, name, genres));
        } while (movieScanner.hasNextInt());

        Scanner ratings = new Scanner(ratingsFile);
        ratings.useDelimiter("::|(\\r)?\\n");

        Session session = new Session("ml1m-import-session", "127.0.0.1");

        System.out.println("\nLoading ratings ...");

        line = 0;
        lastPerc = 0;

        do {
            line++;

            double percentage = line * 100.0 / RATING_COUNT;

            if (Math.floor(percentage) % 10 == 0 && (int) percentage != lastPerc) {
                lastPerc = (int) percentage;
                System.out.print(lastPerc + "% ");
            }

            int userId = ratings.nextInt();
            int movieId = ratings.nextInt();
            int rating = ratings.nextInt();
            String timestampStr = ratings.next();
            Date timestamp = new Date(Long.parseLong(timestampStr));
            Movie movie = movies.get(movieId);

            shopRecommenderService.rateItem(remoteTenant, "" + userId, "" + movieId, Item.DEFAULT_STRING_ITEM_TYPE,
                    movie.getName() + "\nGenres: " + movie.getGenres(), "", "", rating, timestamp, session);
        } while (ratings.hasNextInt());
    }
}
