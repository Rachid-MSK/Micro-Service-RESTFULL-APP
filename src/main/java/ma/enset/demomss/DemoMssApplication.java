package ma.enset.demomss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.domain.Pageable;
import java.util.List;

@SpringBootApplication
public class DemoMssApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMssApplication.class, args);
    }
    @Bean
    CommandLineRunner start(ProduitRepository produitRepository, RepositoryRestConfiguration restConfiguration){
        return args ->{
            restConfiguration.exposeIdsFor(Produit.class);
            produitRepository.save(new Produit(null, "PC", 5000, 10 ));
            produitRepository.save(new Produit(null, "Imprimante", 2000, 10 ));
            produitRepository.save(new Produit(null, "smart phone", 1000, 10 ));
            produitRepository.findAll().forEach(p ->{
                System.out.println(p.getName());
            });
        };

    }

}

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
class Produit{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private double quantity;
}
@RepositoryRestResource
interface ProduitRepository extends JpaRepository<Produit, Long>{
    @RestResource(path="/byName")
    Page<Produit> findByNameContains(@Param("kw") String name, Pageable pageable);
}

@Projection(name="mobile", types=Produit.class)
interface ProduitProjection{
    String getName();
}

@Projection(name="web", types=Produit.class)
interface ProduitProjection2{
    String getName();
    double getPrice();
}

 // @RestController
class ProduitRestController{
    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping(path="/produits")
    public List<Produit> list(){
        return produitRepository.findAll();
    }

    @GetMapping(path="/produits/{id}")
    public Produit getOne(@PathVariable Long id){
        return produitRepository.findById(id).get();
    }

    @PostMapping(path="/produits")
    public Produit save(@RequestBody Produit produit){
        return produitRepository.save(produit);
    }

    @PutMapping(path="/produits/{id}")
    public Produit update(@PathVariable Long id , @RequestBody Produit produit){
        produit.setId(id);
        return produitRepository.save(produit);
    }

    @DeleteMapping(path="/produits/{id}")
    public void delete(@PathVariable Long id){
        produitRepository.deleteById(id);

    }
}
