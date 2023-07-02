package meghana;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eStoreProduct.DAO.OrderDAOViewImp;
import eStoreProduct.model.OrdersMapper;
import eStoreProduct.DAO.customerDAOImp;
import eStoreProduct.model.OrdersViewModel;
import eStoreProduct.model.customerMapper;

public class OrderDAOViewImpTest {  
	private OrderDAOViewImp orderDAO;
	    @Mock
	    private DataSource dataSource;
	    
	    @Mock
	    private JdbcTemplate jdbcTemplate;
	    
	    @BeforeMethod
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        orderDAO = new OrderDAOViewImp(dataSource);
	        orderDAO.jdbcTemplate = jdbcTemplate;
	    }    
	    @Test
	    public void testGetorderProds() {
	        // Mock the expected result
	        List<OrdersViewModel> expectedList = mock(List.class);
	        when(jdbcTemplate.query(anyString(), any(Object[].class), any(OrdersMapper.class))).thenReturn(expectedList);

	        // Call the method
	        List<OrdersViewModel> resultList = orderDAO.getorderProds(1);
	        // Verify the method invocation
	        verify(jdbcTemplate).query(anyString(), any(Object[].class), any(OrdersMapper.class));
	        // Assert the result
	        assertSame(resultList, expectedList);
	    }
    
	    @Test
	    public void testOrdProductById() {
	        // Mock the expected result
	        OrdersViewModel expectedModel = mock(OrdersViewModel.class);
	        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(OrdersMapper.class))).thenReturn(expectedModel);

	        // Call the method
	        OrdersViewModel resultModel = orderDAO.OrdProductById(1, 2);

	        // Verify the method invocation
	        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), any(OrdersMapper.class));

	        // Assert the result
	        assertSame(resultModel, expectedModel);
	    }
 
	    @Test
	    public void testCancelorderbyId() {
	        // Call the method
	        Integer productId = 1;
	        int orderId = 2;
	        orderDAO.cancelorderbyId(productId, orderId);
	        // Verify the method invocation
	        String expectedQuery = "UPDATE slam_OrderProducts SET orpr_shipment_status = 'cancelled' WHERE prod_id = ? and ordr_id=?";
	        verify(jdbcTemplate).update(expectedQuery, productId, orderId);
	    }

	    @Test
	    public void testGetShipmentStatus() {
	        // Mock the jdbcTemplate object
	        //JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
	       // orderDAO.setJdbcTemplate(jdbcTemplate);

	        // Set up the mock to return the expected shipment status
	        Integer productId = 1;
	        int orderId = 2;
	        String expectedStatus = "shipped";
	        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn(expectedStatus);

	        // Call the method
	        String resultStatus = orderDAO.getShipmentStatus(productId, orderId);

	        // Verify the method invocation
	        String expectedQuery = "SELECT orpr_shipment_status FROM slam_orderproducts WHERE prod_id = ? and ordr_id=?";
	        verify(jdbcTemplate).queryForObject(expectedQuery, new Object[] { productId, orderId }, String.class);

	        // Assert the result
	        assertEquals(resultStatus, expectedStatus);
	    }

    
	    @Test
	    public void testSortProductsByPrice() {
	        // Create test data
	        List<OrdersViewModel> ordersList = new ArrayList<>();
	        OrdersViewModel order1 = new OrdersViewModel();
	        order1.setPrice(10.0);
	        ordersList.add(order1);
	        OrdersViewModel order2 = new OrdersViewModel();
	        order2.setPrice(5.0);
	        ordersList.add(order2);
	        OrdersViewModel order3 = new OrdersViewModel();
	        order3.setPrice(8.0);
	        ordersList.add(order3);

	        // Call the method with "lowToHigh" sortOrder
	        String lowToHighSortOrder = "lowToHigh";
	        List<OrdersViewModel> sortedListLowToHigh = orderDAO.sortProductsByPrice(ordersList, lowToHighSortOrder);

	        // Verify the list is sorted in ascending order
	        for (int i = 0; i < sortedListLowToHigh.size() - 1; i++) {
	            assertTrue(sortedListLowToHigh.get(i).getPrice() <= sortedListLowToHigh.get(i + 1).getPrice());
	        }

	        // Call the method with "highToLow" sortOrder
	        String highToLowSortOrder = "highToLow";
	        List<OrdersViewModel> sortedListHighToLow = orderDAO.sortProductsByPrice(ordersList, highToLowSortOrder);

	        // Verify the list is sorted in descending order
	        for (int i = 0; i < sortedListHighToLow.size() - 1; i++) {
	            assertTrue(sortedListHighToLow.get(i).getPrice() >= sortedListHighToLow.get(i + 1).getPrice());
	        }
	    }

	    @Test
	    public void testFilterProductsByPriceRange() {
	        // Create test data
	        List<OrdersViewModel> products = new ArrayList<>();
	        OrdersViewModel product1 = new OrdersViewModel();
	        product1.setPrice(10.0);
	        products.add(product1);
	        OrdersViewModel product2 = new OrdersViewModel();
	        product2.setPrice(5.0);
	        products.add(product2);
	        OrdersViewModel product3 = new OrdersViewModel();
	        product3.setPrice(8.0);
	        products.add(product3);

	        // Define the price range
	        double minPrice = 6.0;
	        double maxPrice = 9.0;

	        // Call the method to filter products within the price range
	        List<OrdersViewModel> filteredList = orderDAO.filterProductsByPriceRange(products, minPrice, maxPrice);

	        // Verify that the filtered list contains only the products within the price range
	        for (OrdersViewModel product : filteredList) {
	            double price = product.getPrice();
	            assertTrue(price >= minPrice && price <= maxPrice);
	        }
	    }

    
	    @Test
	    public void testAreAllProductsCancelled() {
	        // Mock the expected result
	        int expectedCount = 0;
	        when(jdbcTemplate.queryForObject(anyString(), any(Class.class), anyInt())).thenReturn(expectedCount);

	        // Call the method with a specific orderId
	        int orderId = 123;
	        boolean result = orderDAO.areAllProductsCancelled(orderId);

	        // Verify the method invocation
	        verify(jdbcTemplate).queryForObject(
	            "SELECT COUNT(*) FROM slam_OrderProducts WHERE ordr_id = ? AND orpr_shipment_status != 'cancelled'",
	            Integer.class,
	            orderId
	        );

	        // Assert the result
	        assertTrue(result);
	    }

	    @Test
	    public void testUpdateOrderShipmentStatus() {
	        // Call the method with specific orderId and shipmentStatus values
	        int orderId = 123;
	        String shipmentStatus = "shipped";
	        orderDAO.updateOrderShipmentStatus(orderId, shipmentStatus);

	        // Verify the method invocation
	        verify(jdbcTemplate).update(
	            "UPDATE slam_Orders SET ordr_shipment_status = ? WHERE ordr_id = ?",
	            shipmentStatus,
	            orderId
	        );
	    }

}
