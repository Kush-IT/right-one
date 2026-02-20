@RestController
@RequestMapping("/api/startup")
public class StartupController {

    @Autowired
    private DealRepository dealRepo;

    @GetMapping("/deals/{id}")
    public List<Deal> deals(@PathVariable Long id){
        return dealRepo.findByStartupId(id);
    }

    @PutMapping("/deal/{id}")
    public Deal update(@PathVariable Long id,
                       @RequestParam String status){
        Deal d=dealRepo.findById(id).orElseThrow();
        d.setStatus(status);
        return dealRepo.save(d);
    }
}
