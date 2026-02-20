@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DealRepository dealRepo;

    @GetMapping("/summary")
    public Map<String,Object> summary(){

        Map<String,Object> map=new HashMap<>();

        map.put("totalUsers",userRepo.count());

        map.put("totalDeals",dealRepo.count());

        map.put("acceptedDeals",
                dealRepo.findAll().stream()
                        .filter(d->d.getStatus().equals("Accepted"))
                        .count());

        map.put("totalFunding",
                dealRepo.findAll().stream()
                        .filter(d->d.getStatus().equals("Accepted"))
                        .mapToDouble(Deal::getAmount)
                        .sum());

        return map;
    }
}
