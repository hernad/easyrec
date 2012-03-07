/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.utils;

import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.TenantVO;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.Session;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.NamedConfigurationService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.spring.cli.AbstractDependencyInjectionSpringCLI;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.model.Version;


/**
 * DOCUMENT ME!
 *
 * @author pmarschik
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Movielens100kImporter extends AbstractDependencyInjectionSpringCLI {
    private static final Logger logger = Logger.getLogger(Movielens1MImporter.class.getName());
    private static final double RATING_COUNT = 100000;
    private static final double MOVIE_COUNT = 1682;
    private static final double USER_COUNT = 943;

    private OperatorDAO operatorDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private ShopRecommenderService shopRecommenderService;
    private TenantService tenantService;
    private ClusterService clusterService;
    private TypeMappingService typeMappingService;
    private IDMappingDAO idMappingDAO;
    private ItemTypeDAO itemTypeDAO;
    private NamedConfigurationService namedConfigurationService;
    private PluginRegistry pluginRegistry;

    private HashMap<Integer, String> clusters;

    public static void main(String[] args) {
        Movielens100kImporter importer = new Movielens100kImporter();
        importer.processCommandLineCall(args);
        System.exit(0);
    }

    public void setNamedConfigurationService(NamedConfigurationService namedConfigurationService) {
        this.namedConfigurationService = namedConfigurationService;
    }

    public void setItemTypeDAO(ItemTypeDAO itemTypeDAO) {
        this.itemTypeDAO = itemTypeDAO;
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

    public ClusterService getClusterService() {
        return clusterService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public TypeMappingService getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public IDMappingDAO getidMappingDAO() {
        return idMappingDAO;
    }

    public void setidMappingDAO(IDMappingDAO idMappingDAO) {
        this.idMappingDAO = idMappingDAO;
    }

    public IDMappingDAO getIdMappingDAO() {
        return idMappingDAO;
    }

    public void setIdMappingDAO(IDMappingDAO idMappingDAO) {
        this.idMappingDAO = idMappingDAO;
    }

    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[]{"spring/web/importer/movielens/AllInOne_Movielens100k.xml"};
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

    public void initClusters() {
        clusters = new HashMap<Integer, String>();
        clusters.put(0, "Unknown");
        clusters.put(1, "Action");
        clusters.put(2, "Adventure");
        clusters.put(3, "Animation");
        clusters.put(4, "Children");
        clusters.put(5, "Comedy");
        clusters.put(6, "Crime");
        clusters.put(7, "Documentary");
        clusters.put(8, "Drama");
        clusters.put(9, "Fantasy");
        clusters.put(10, "Filmnoir");
        clusters.put(11, "Horror");
        clusters.put(12, "Musical");
        clusters.put(13, "Mystery");
        clusters.put(14, "Romance");
        clusters.put(15, "Scifi");
        clusters.put(16, "Thriller");
        clusters.put(17, "War");
        clusters.put(18, "Western");
    }

    @Override
    protected void usage() {
        System.out.println("Usage: java -...ImporterCLI <path_to_movielens_100k> <useClusters>");
    }

    private void parseData(Operator operator, File ratingsFile, Map<Integer, Movie> movies, String tenantName,
                           Session session, boolean useClusters, boolean useItemTypes)
            throws FileNotFoundException, NumberFormatException {
        if (remoteTenantDAO.exists(tenantName)) {
            System.out.println("Tenant " + tenantName + " already exists. SKIPPING import.");

            return;
        }

        String tenantDescription = "MovieLens data sets were collected by the GroupLens Research Project\n" +
                "at the University of Minnesota.\n" + "\n" + "This data set consists of:\n" +
                "\t* 100,000 ratings (1-5) from 943 users on 1682 movies.\n" +
                "\t* Each user has rated at least 20 movies.";

        TenantVO tenant = new TenantVO(tenantName, tenantDescription, 1, 5, 2.5);
        tenantService.insertTenantWithTypes(tenant, null);

        System.out.println("\nTenant got id: " + tenant.getId());

        remoteTenantDAO.update(operator.getOperatorId(), tenant.getId(), "", tenantDescription);
        tenantService.updateConfigProperty(tenant.getId(), RemoteTenant.AUTO_ARCHIVER_ENABLED, "false");
        tenantService.updateConfigProperty(tenant.getId(), RemoteTenant.AUTO_ARCHIVER_TIME_RANGE,
                RemoteTenant.AUTO_ARCHIVER_DEFAULT_TIME_RANGE);
        // enable backtracking by default
        tenantService.updateConfigProperty(tenant.getId(), RemoteTenant.BACKTRACKING, "true");
        // enable auto rule mining by default
        tenantService.updateConfigProperty(tenant.getId(), RemoteTenant.SCHEDULER_ENABLED, "false");
        tenantService.updateConfigProperty(tenant.getId(), RemoteTenant.SCHEDULER_EXECUTION_TIME,
                RemoteTenant.SCHEDULER_DEFAULT_EXECUTION_TIME);
        namedConfigurationService.setupDefaultConfiguration(tenant.getId());

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenant.getId());

        Scanner ratings = new Scanner(ratingsFile);
        ratings.useDelimiter("\\t|(\\r)?\\n");

        System.out.println("\nLoading ratings ...");

        int line = 0;
        int lastPerc = 0;

        if (useItemTypes) {
            for (String itemTypeName : clusters.values()) {
                itemTypeDAO.insertOrUpdate(tenant.getId(), "GENRE_" + itemTypeName.toUpperCase(), true);
            }
        }

        do {
            line++;

            double percentage = (line * 100.0) / RATING_COUNT;

            if (((Math.floor(percentage) % 10) == 0) && ((int) percentage != lastPerc)) {
                lastPerc = (int) percentage;
                System.out.print(lastPerc + "% ");
            }

            int userId = ratings.nextInt();
            int movieId = ratings.nextInt();
            int rating = ratings.nextInt();
            String timestampStr = ratings.next();
            Date timestamp = new Date(Long.parseLong(timestampStr));
//            Date timestamp = new Date();
            Movie movie = movies.get(movieId);

            String itemType = Item.DEFAULT_STRING_ITEM_TYPE;

            if (useItemTypes) {
                int genreId = movie.getGenres().nextSetBit(0);

                if (genreId >= 0 && clusters.containsKey(genreId)) {
                    String clusterName = clusters.get(genreId);

                    if (clusterName != null)
                        itemType = "GENRE_" + clusterName.toUpperCase();
                }
            }

            shopRecommenderService.rateItem(remoteTenant, "" + userId, "" + movieId, itemType,
                    movie.getName() + " Genres: " + movie.getGenres(), movie.getImdbUrl(), movie.getGeneratedImageUrl(),
                    rating, timestamp, session);           
            // always also view with type item
            shopRecommenderService.viewItem(remoteTenant, "" + userId, "" + movieId, Item.DEFAULT_STRING_ITEM_TYPE,
                    movie.getName() + " Genres: " + movie.getGenres(), movie.getImdbUrl(), movie.getGeneratedImageUrl(),
                    timestamp, session);
//           use generic sendAction method for view actions for testing 
//            shopRecommenderService.sendAction(remoteTenant, "" + userId, "" + movieId, Item.DEFAULT_STRING_ITEM_TYPE, 
//                        movie.getName() + " Genres: " + movie.getGenres(), movie.getImdbUrl(), movie.getGeneratedImageUrl(),
//                        "VIEW", rating, timestamp, session);
            
        } while (ratings.hasNextInt());

        if (useClusters) {
            System.out.println("Creating Clusters for tenant!\n");
            for (String clusterName : clusters.values()) {
                try {
                    clusterService.addCluster(remoteTenant.getId(), clusterName, "The Genre " + clusterName,
                            clusterService.getClustersForTenant(remoteTenant.getId()).getRoot().getName());
                } catch (ClusterException ce) {
                    System.out
                            .println("An error occured creating the clusters for tenant " + remoteTenant.getStringId() +
                                    ": " + ce.getMessage());
                }
            }
            System.out.println("Done!");
            System.out.println("Adding movies to clusters:\n");
            for (Movie movie : movies.values()) {
                for (int i = movie.getGenres().nextSetBit(0); i >= 0; i = movie.getGenres().nextSetBit(i + 1)) {
                    // operate on index i here
                    try {
                        if ((i < 0) || (i > 18)) {
                            System.out.println("Unknown Genre: " + i + " " + movie.getName());
                        } else {
                            clusterService.addItemToCluster(remoteTenant.getId(), clusters.get(i),
                                    idMappingDAO.lookup(Integer.toString(movie.getId())),
                                    typeMappingService.getIdOfItemType(remoteTenant.getId(),
                                            Item.DEFAULT_STRING_ITEM_TYPE));
                        }
                    } catch (ClusterException ce) {
                        System.out
                                .println("An error occured adding item " + movie.getName() + " to cluster " +
                                        clusters.get(i) + ": " + ce.getMessage());
                    }
                }
            }
            System.out.println("Done!");
        }
    }

    private Map<Integer, Movie> parseMovies(File moviesFile) throws FileNotFoundException {
        Map<Integer, Movie> movies = new TreeMap<Integer, Movie>();

        FileInputStream fsi = new FileInputStream(moviesFile);
        Scanner movieScanner = new Scanner(fsi, "UTF-8");
        movieScanner.useDelimiter("\\||\\r?\\n");

        System.out.println("Loading movies ...");

        int line = 0;
        int lastPerc = 0;

        do {
            line++;

            double percentage = (line * 100.0) / MOVIE_COUNT;

            if (((Math.floor(percentage) % 10) == 0) && ((int) percentage != lastPerc)) {
                lastPerc = (int) percentage;
                System.out.print(lastPerc + "% ");
            }

            int id = movieScanner.nextInt();
            String name = movieScanner.next();
            String releaseDate = movieScanner.next();
            String videoReleaseDate = movieScanner.next();
            String imdbUrl = movieScanner.next();

            try {
                imdbUrl = URLDecoder.decode(imdbUrl, "UTF-8").replaceAll(" ", "+");
            } catch (Exception ignored) {
            }

            BitSet genres = new BitSet();

            int idx = 0;
            genres.set(idx++, movieScanner.nextInt() == 1); // unknown
            genres.set(idx++, movieScanner.nextInt() == 1); // action
            genres.set(idx++, movieScanner.nextInt() == 1); // adventure
            genres.set(idx++, movieScanner.nextInt() == 1); // animation
            genres.set(idx++, movieScanner.nextInt() == 1); // childrens
            genres.set(idx++, movieScanner.nextInt() == 1); // comedy
            genres.set(idx++, movieScanner.nextInt() == 1); // crime
            genres.set(idx++, movieScanner.nextInt() == 1); // documentary
            genres.set(idx++, movieScanner.nextInt() == 1); // drama
            genres.set(idx++, movieScanner.nextInt() == 1); // fantasy
            genres.set(idx++, movieScanner.nextInt() == 1); // filmnoir
            genres.set(idx++, movieScanner.nextInt() == 1); // horror
            genres.set(idx++, movieScanner.nextInt() == 1); // musical
            genres.set(idx++, movieScanner.nextInt() == 1); // mystery
            genres.set(idx++, movieScanner.nextInt() == 1); // romance
            genres.set(idx++, movieScanner.nextInt() == 1); // scifi
            genres.set(idx++, movieScanner.nextInt() == 1); // thriller
            genres.set(idx++, movieScanner.nextInt() == 1); // war
            genres.set(idx, movieScanner.nextInt() == 1); // western

            String imageUrl = "";

            /*
            try {
                // TODO need better name handling, e.g. remove all braces and text inside braces,
                // mov "The" to the front etc.

                // strip the year
                String queryName = name.substring(0, name.length() - 7);

                if (queryName.endsWith(", The"))
                    queryName = "The " + queryName.substring(0, queryName.length() - 6);

                //queryName = URLEncoder.encode(queryName, "UTF-8");
                queryName = queryName.replaceAll(" ", "%20");
                int queryYear = Integer.parseInt(releaseDate.substring(7));
                // built using http://www.freebase.com/queryeditor
                String freebaseQuery = "http://www.freebase" +
                        ".com/api/service/mqlread?query={%20%22query%22%3A%20%5B{%20%22%2Fcommon%2Ftopic%2Fimage%22" +
                        "%3A%20{%20%22id%22%3A%20null%2C%20%22limit%22%3A%201%2C%20%22optional%22%3A%20true%20}%2C%" +
                        "20%22FBID96%3Ainitial_release_date%22%3A%20%5B{%20%22type%22%3A%20%22%2Ftype%2Fdatetime%22" +
                        "%2C%20%22value%3C%22%3A%20%22" + (queryYear + 1) + "%22%2C%20%22value%3E%3D%22%3A%20%22" +
                        queryYear + "%22%20}%5D%2C%20%22id%22%3A%20null%2C%20%22limit%22%3A%201%2C%20%22name%22%3A" +
                        "%20null%2C%20%22q0%3Aname~%3D%22%3A%20%22*" + queryName +
                        "*%22%2C%20%22s0%3Atype%22%3A%20%5B{" +
                        "%20%22id%22%3A%20%22%2Ffilm%2Ffilm%22%2C%20%22link%22%3A%20%5B{%20%22timestamp%22%3A%20%5B" +
                        "{%20%22optional%22%3A%20true%2C%20%22type%22%3A%20%22%2Ftype%2Fdatetime%22%2C%20%22value%2" +
                        "2%3A%20null%20}%5D%2C%20%22type%22%3A%20%22%2Ftype%2Flink%22%20}%5D%2C%20%22type%22%3A%20%" +
                        "22%2Ftype%2Ftype%22%20}%5D%2C%20%22type%22%3A%20%22%2Ffilm%2Ffilm%22%20}%5D%20}";

                URL url = new URL(freebaseQuery);
                URLConnection connection = url.openConnection();
                InputStream textInputStream = connection.getInputStream();

                StringBuilder content = new StringBuilder();
                int curChar = -1;

                while ((curChar = textInputStream.read()) != -1)
                    content.append((char) curChar);

                JSONObject response = new JSONObject(content.toString());
                JSONArray resultArray = response.getJSONArray("result");

                if (resultArray.length() > 0) {
                    String imageId = resultArray.getJSONObject(0).getJSONObject(
                            "/common/topic/image").getString("id");
                    imageUrl = "http://img.freebase.com/api/trans/image_thumb" + imageId + "?maxwidth=1024";
                }
            } catch (Exception ignored) {
                System.out.println(ignored);
            }
            */

            movies.put(id, new Movie(id, name, releaseDate, videoReleaseDate, imdbUrl, genres, imageUrl));
        } while (movieScanner.hasNextInt());

        movieScanner.close();

        return movies;
    }

    private void processCommandLineCallEx(String[] args) throws Exception {
        String pathToDataset = "C://projects//easyrec//movielens//small//ml-data"; //"C:\\DATA\\datasets\\ml100k";

        boolean useClusters = true;
        boolean useItemTypes = true;

        if (args.length != 1) {
            if (!new File(pathToDataset).exists()) usage();
        } else pathToDataset = args[0];

        File datasetFile = new File(pathToDataset);
        File moviesFile = new File(datasetFile.getAbsolutePath() + File.separator + "u.item");
        File ratingsFile = new File(datasetFile.getAbsolutePath() + File.separator + "u.data");

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

        pluginRegistry.installPlugin(URI.create("http://www.easyrec.org/plugins/ARM"), new Version("0.97"));
        
        Map<Integer, Movie> movies = parseMovies(moviesFile);

        //noinspection ConstantConditions
        if (useClusters || useItemTypes)
            initClusters();

        Operator operator = operatorDAO.get("easyrec");

        Session session = new Session("ml100k-import-session", "127.0.0.1");
        parseData(operator, ratingsFile, movies, "Movielens_100k", session, useClusters, useItemTypes);

        for (int i = 0; i < 5; i++) {
            ratingsFile = new File(datasetFile.getAbsolutePath() + File.separator + "u" + (i + 1) + ".base");

            parseData(operator, ratingsFile, movies, "Movielens_100k_" + (i + 1), session, useClusters, useItemTypes);
        }
    }

    private static class Movie {
        private BitSet genres;
        private String imdbUrl;
        private String name;
        private String releaseDate;
        private String videoReleaseDate;
        private String generatedImageUrl;
        private int id;

        public Movie(int id, String name, String releaseDate, String videoReleaseDate, String imdbUrl, BitSet genres,
                     String generatedImageUrl) {
            this.id = id;
            this.name = name;
            this.releaseDate = releaseDate;
            this.videoReleaseDate = videoReleaseDate;
            this.imdbUrl = imdbUrl;
            this.genres = genres;
            this.generatedImageUrl = generatedImageUrl;
        }

        public BitSet getGenres() {
            return genres;
        }

        public int getId() {
            return id;
        }

        public String getImdbUrl() {
            return imdbUrl;
        }

        public String getName() {
            return name;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getVideoReleaseDate() {
            return videoReleaseDate;
        }

        public String getGeneratedImageUrl() {
            return generatedImageUrl;
        }
    }
}
