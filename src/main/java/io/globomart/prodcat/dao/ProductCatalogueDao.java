package io.globomart.prodcat.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.globomart.prodcat.dto.Product;
import io.globomart.prodcat.entities.ProductEntity;

public abstract class ProductCatalogueDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCatalogueDao.class);

	public static ProductEntity createProduct(Product product) {
		EntityManager theManager = PersistanceUtil.createEntityManager();
		theManager.getTransaction().begin();
		ProductEntity productEntity = new ProductEntity(product.getBrandName(), product.getModel(), product.getColor(),product.getProductType());
		theManager.persist(productEntity);
		theManager.getTransaction().commit();
		LOGGER.info("Product created successfully for " + product);
		return productEntity;
	}

	public static List<ProductEntity> getProducts(Map<String, String> filter) {
		EntityManager entityManager = PersistanceUtil.createEntityManager();
		List<ProductEntity> result = entityManager.createQuery(queryBuilder(entityManager, filter)).getResultList();
		return result;
	}

	public static ProductEntity getProducts(int prodId) {
		EntityManager entityManager = PersistanceUtil.createEntityManager();
		return entityManager.find(ProductEntity.class, prodId);
	}

	private static CriteriaQuery<ProductEntity> queryBuilder(EntityManager entityManager,
			Map<String, String> searchCriteria) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);
		Root<ProductEntity> products = criteriaQuery.from(ProductEntity.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (searchCriteria != null) {
			for (String searchKey : searchCriteria.keySet()) {
				predicates.add(criteriaBuilder.equal(products.get(searchKey), searchCriteria.get(searchKey)));
			}
		}
		CriteriaQuery<ProductEntity> query = criteriaQuery.select(products)
				.where(predicates.toArray(new Predicate[] {}));
		return query;

	}
}
