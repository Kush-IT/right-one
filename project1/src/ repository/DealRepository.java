public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByStartupId(Long startupId);
}
