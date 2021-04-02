# Demo-service-xxx系列的使用说明
这是一套简单易用、支持DDD与微服务的技术框架，它一方面演示了整个微服务的技术架构，同时为微服务下如何打造支持快速交付的技术中台提出了全新的思想。
该示例包含如下项目：

```bash
demo-parent             本示例所有项目的父项目，它集成了springboot, springcloud，并定义各项目如何maven打包
demo-service-eureka     微服务注册中心eureka，特别是高可用eureka集群
demo-service-config     微服务配置中心config
demo-service-turbine    各微服务断路器运行状况监控器turbine
demo-service-zuul       服务网关zuul
demo-service-parent     各业务微服务（无数据库访问）的父项目
demo-service-support    各业务微服务（无数据库访问）底层技术框架
demo-service-customer   用户管理微服务（无数据库访问）
demo-service-product    产品管理微服务（无数据库访问）
demo-service-supplier   供应商管理微服务（无数据库访问）
demo-service2-parent    各业务微服务（有数据库访问）的父项目
demo-service2-support   各业务微服务（有数据库访问）底层技术框架
demo-service2-customer  用户管理微服务（有数据库访问）
demo-service2-product   产品管理微服务（有数据库访问）
demo-service2-supplier  供应商管理微服务（有数据库访问）
demo-service2-order     订单管理微服务（有数据库访问）
```
本框架简单易用、支持DDD与微服务，它有如下几个特点：
## 1. 易于业务变更与维护
我们现在处于快速变化的时代，一方面市场与业务在快速变更，另一方面技术架构在快速更迭。激烈的市场竞争要求技术团队需要更快的交付速度，但许多团队由于项目编码过于繁杂，变更越来越困难，维护成本越来越高，交付速度越来越慢。代码编写越简洁，日后维护的成本就越低，更新速度就越快。因此，本框架打造了一个使业务编写更加简单快捷的技术框架。

### 1）单Controller的设计
在本框架中，不需要为每个业务模块编写Controller，整个系统只有2个Controller（增删改操作一个，查询一个）。通过规范，首先让业务开发人员在开发代码时，将前端的Json与后台的值对象对应起来，那么本框架就通过反射，自动地将前端Json中的数据，转换成后台的值对象，然后通过反射去调用相应的Service。这样的设计，既避免了以往设计中写大量的Controller，使系统开发成本高而不易维护与变更，又是的业务开发人员没有机会将业务代码写到Controller中，而是规范地编写到Bus/Service中，从而规范了系统分层，有利于日后的维护。

在使用单Controller以后，前端所有功能的增删改操作，以及基于id的get/load操作，都是访问的OrmController。前端在访问OrmController时，输入如下http请求：

```bash
http://localhost:9003/orm/{bean}/{method}
```
例如：

```bash
GET请求：http://localhost:9003/orm/product/deleteProduct?id=P00006
```
或者

```bash
POST请求：http://localhost:9003/orm/product/saveProduct
"id=P00006&name=ThinkPad+T220&price=4600&unit=%E4%B8%AA&supplierId=S0002&classify=%E5%8A%9E%E5%85%AC%E7%94%A8%E5%93%81"
```

{bean}是配置在Spring中的bean.id，{method}是该bean中需要调用的方法（注意，此处不支持方法的重载，如果出现重载，它将去调用同名方法中的最后一个）。

如果要调用的方法有值对象，必须将值对象放在方法的第一个参数上。如果要调用的方法既有值对象，又有其它参数，则值对象中的属性与其它参数都这样调用：
要调用的方法：saveProduct(product, saveMode);

```bash
POST请求：http://localhost:9003/orm/product/saveProduct
"id=P00006&name=ThinkPad+T220&price=4600&unit=%E4%B8%AA&supplierId=S0002&classify=%E5%8A%9E%E5%85%AC%E7%94%A8%E5%93%81&saveMode=1"
```

注意：OrmController不包含任何权限校验，配置在Spring中的所有bean中的所有方法都可以被前端调用，因此通常需要在OrmController之前进行一个权限校验，来规范前端可以调用的方法。可以使用一个服务网关或filter进行校验。

### 2）单Dao的设计
在本框架中，不需要为每个业务模块编写Dao，所有的Service都只需要配一个Dao。那么如何进行持久化呢？将每个值对象对应的表，以及值对象中每个属性对应的字段，通过vObj.xml配置文件进行对应，那么通用的BasicDao就可以通过配置文件形成SQL，并最终完成数据库持久化操作。vObj.xml配置文件：

```bash
<?xml version="1.0" encoding="UTF-8"?>
<vobjs>
  <vo class="com.demo2.trade.entity.Customer" tableName="Customer">
    <property name="id" column="id" isPrimaryKey="true"></property>
    <property name="name" column="name"></property>
    <property name="sex" column="sex"></property>
    <property name="birthday" column="birthday"></property>
    <property name="identification" column="identification"></property>
<property name="phone_number" column="phone_number"></property>
  </vo>
</vobjs>
```

值对象中可以设计很多属性变量，但只有最终做持久化的属性变量才需要配置。这样可以使值对象的设计具有更大的空间去做更多的转换与操作（充血模型的设计）。

有了以上设计以后，每个Service都必须有一个dao的属性变量，并在Spring中统一注入BasicDao（如果要使用DDD的功能支持，注入Repository；如果要使用Redis缓存，注入RepositoryWithCache）。

有了以上设计，业务开发人员只需要在系统中编写前端界面、Service与值对象，就可以完成业务开发，而Service与值对象的设计都源于领域驱动设计。

### 3）数据查询的设计
本框架采用CQRS（命令与查询职责分离）的设计模式，所有的SQL查询都使用另一个Controller（QueryController）来进行查询（注意：基于id的get/load方法使用OrmController来查询）。

在进行查询时，前端输入http请求：

```bash
http://localhost:9003/query/{bean}
```
该请求既可以接收POST请求，也可以接收GET请求。{bean}是配置在Spring中的Service。QueryController通过该请求，在Spring中找到Service，并调用Service.query(map)进行查询，此处的map就是该请求传递的所有查询参数。

本框架在查询时采用了单Service的设计，既所有的查询都是配置QueryService进行查询，但注入的是不同的Dao，就可以完成各自不同的查询。每个Dao都是通过MyBatis框架，注入同一个Dao但配置不同的mapper，就可以完成不同的查询。因此，先配置MyBatis的Mapper文件诸如：

```bash
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"   
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.demo2.trade.query.dao.CustomerMapper">
	<!--筛选条件-->
	<sql id="searchParam">
		<if test="id != '' and id != null">
			and id = #{id}
		</if>
	</sql>
	
	<!--求count判断-->
	<sql id="isCount1">
		<if test="count == null  and notCount ==1">
			select count(*) from (
		</if>
	</sql>
	<sql id="isCount2">
		<if test="count == null  and notCount ==1">
			) count
		</if>
	</sql>	
	
	<!--是否分页判断-->
	<sql id="isPage">
		<if test="size != null  and size !=''">
			limit #{size} offset #{firstRow} 
		</if>
		<if test="size ==null  or size ==''">
			<if test="pageSize != null  and pageSize !=''">
				limit #{pageSize} offset #{startNum} 
			</if>
		</if>
	</sql>
	
	<select id="query" parameterType="java.util.HashMap" resultType="com.demo2.trade.entity.Customer">
		   <include refid="isCount1"/> 
		   	 	SELECT * FROM Customer WHERE 1 = 1
				<include refid="searchParam"/>
				<include refid="isPage"/>
		   <include refid="isCount2"/>
	</select>
</mapper>
```
然后将其注入到Spring中，完成相应的配置，就可以进行查询：

```bash
	<bean id="customerQry" class="com.demo2.support.service.impl.QueryServiceImpl">
		<property name="queryDao">
			<bean class="com.demo2.support.dao.impl.QueryDaoMybatisImpl">
				<property name="sqlMapper" value="com.demo2.trade.query.dao.CustomerMapper.query"></property>
			</bean>
		</property>
	</bean>
```
此外，如果希望在查询前与查询后加入某些处理程序，则继承QueryServiceImpl并重载beforeQuery或afterQuery，例如：

```bash
/**
 * The implement of the query service for products.
 * @author fangang
 */
public class ProductQueryServiceImpl extends QueryServiceImpl {
	@Autowired
	private SupplierService supplierService;
	@Override
	protected ResultSet afterQuery(Map<String, Object> params,
			ResultSet resultSet) {
		@SuppressWarnings("unchecked")
		List<Product> list = (List<Product>)resultSet.getData();
		
		List<Long> listOfIds = new ArrayList<>();
		for(Product product : list) {
			Long supplierId = product.getSupplierId();
			listOfIds.add(supplierId);
			//Supplier supplier = supplierService.loadSupplier(supplierId);
			//product.setSupplier(supplier);
		}
		List<Supplier> listOfSuppliers = supplierService.loadSuppliers(listOfIds);
		
		Map<Object, Supplier> mapOfSupplier = new HashMap<>();
		for(Supplier supplier : listOfSuppliers) {
			mapOfSupplier.put(supplier.getId(), supplier);
		}
		
		for(Product product : list) {
			Long supplierId = product.getSupplierId();
			Supplier supplier = mapOfSupplier.get(supplierId);
			product.setSupplier(supplier);
		}
		
		resultSet.setData(list);
		return resultSet;
	}
}
```
此时，在Spring中配置的则是该ProductQueryServiceImpl实现类。

## 2. 易于架构演化
互联网是一个快速技术更迭的时代，但经历了互联网转型，未来还将经历微服务转型、大数据转型，以及5G物联网的转型，如何让系统易于快速进行架构演化显得尤为重要。然而，以往的许多遗留系统存在的普遍的弊病都是，业务代码与技术框架紧耦合，开发人员在业务编码时往往直接去调用底层的某个技术框架。这样的设计，但该技术框架需要被替换掉时却发现，大量业务代码都需要修改。这样的技术栈改造，成本又高，风险又大，不利于工程实践。微服务基于的六边形架构与Bob大叔编写的《整洁架构》都不约而同地提出，业务代码必须与技术框架解耦。

整洁架构（The Clean Architecture）是Robot C. Martin（业界称为Bob大叔）在《架构整洁之道》这本书中提出来的架构设计思想。整洁架构设计以圆环的形式把系统分成了几个不同的部分，其中心是业务实体（Entity）与业务应用（Application），业务实体就是领域模型中的实体与值对象，业务应用就是面向用户的那些服务（Service）。它们合起来组成了业务领域层，也就是通过领域模型的分析，然后运用充血模型或者贫血模型，从而形成的业务代码的实现。整洁架构的最外层是各种技术框架，包括与用户UI的交互、客户与服务器的网络交互、与硬件设备与数据库的交互，以及与其它外部系统的交互。而整洁架构的精华在中间的适配器层，它通过适配器将核心的业务代码与外围的技术框架进行解耦。因此，如何设计这个适配层，让业务代码与技术框架解耦，让业务开发团队与技术架构团队各自独立地工作，成为了整洁架构落地的核心。

为了实践“业务代码与技术框架解耦”，本框架通过单Controller、单Dao与其它底层接口层，打造纯洁的Service，与技术框架解耦。

### 1）放弃注解的方式，采用XML文件
虽然当下注解比较流行，并且有诸多优势，但最大的问题是会带来对框架的依赖。因此本框架在设计上，虽然Controller、Dao以及其它功能设计上使用注解，但基于本框架进行的业务开发，包括Spring的配置、MyBatis的配置、vObj的配置，建议都采用XML文件的形式，而不要采用注解。

### 2）该微服务的设计更易于技术更迭
将本框架转型成微服务架构时，聚合层的微服务使用本框架的单Controller，使得只有OrmController、QueryController等个别框架代码与SpringMVC耦合，而与业务代码不耦合，有利于日后MVC层技术更迭。本框架建议采用Feign接口，使得在跨微服务调用时，Service与Springcloud不耦合，只有对外接口与Feign耦合，有利于日后微服务拆分的时候降低维护成本。

原子服务层的微服务使用本框架的单Controller时，就使得原子服务层对外开放的API接口，不是通过Service对外开放，而是通过Controller对外开放。这样的设计，使得原子服务层的Service不用写Springcloud注解，避免了与Springcloud耦合。同时，通过单Dao避免了Service与数据库耦合。纯洁不带任何技术框架引用的Service，使得系统在日后技术栈更迭时（比如由Springcloud向Istio转型），更加简便易行。

## 3. 支持领域驱动的技术框架
在系统规模越来越庞大，业务规则越来越复杂的今天，领域驱动设计往往成为团队最终的选择。通过领域驱动设计将复杂的业务映射成领域模型，然后再将领域模型去指导程序开发。这样，当需求变更时，就将变更需求还原到真实世界，然后用真实世界映射到领域模型的变更，最后通过领域模型的变更指导软件的变更。通过这样的设计，就可以让开发团队不论经历多少轮变更，都能保持高质量的设计。

但是，要实践领域驱动设计，需要一套技术架构来支撑。传统的领域驱动设计，一方面需要在各个层次进行数据对象的格式转换，另一方面又要为每个业务模块编写DDD仓库与DDD工厂。这样的设计使得系统编码复杂，不利于日后维护。因此，本框架采用统一数据建模、内置聚合的实现、通用仓库和工厂，来简化DDD业务开发。

### 1）统一数据建模
本框架在通过vObj.xml进行数据建模的时候，加入了join标签。当某个值对象在进行查询时需要进行join操作，本框架不建议将join操作写入SQL语句中，而是进行如下配置：

```bash
  <vo class="com.demo2.trade.entity.Product" tableName="Product">
    <property name="id" column="id" isPrimaryKey="true"></property>
    <property name="name" column="name"></property>
    <property name="price" column="price"></property>
    <property name="unit" column="unit"></property>
    <property name="classify" column="classify"></property>
    <property name="supplier_id" column="supplier_id"></property>
    <join name="supplier" joinKey="supplier_id" joinType="manyToOne" class="com.demo2.trade.entity.Supplier"></join>
  </vo>
```
在Product值对象中加入Supplier属性并进行以上配置，则Product在进行get/load操作或query操作时，可以自动补填Supplier。为了实现补填功能，Service在dao注入时，应当注入repository而不是basicDao。在进行查询时，bean也应当配置AutofillQueryServiceImpl并配置其dao：

```bash
	<bean id="productQry" class="com.demo2.support.repository.AutofillQueryServiceImpl">
		<property name="queryDao">
			<bean class="com.demo2.support.dao.impl.QueryDaoMybatisImpl">
				<property name="sqlMapper" value="com.demo2.trade.query.dao.ProductMapper.query"></property>
			</bean>
		</property>
		<property name="dao" ref="basicDao"></property>
	</bean>
```
该配置也支持oneToOne、manyToOne、oneToMany，但不支持manyToMany（基于性能的考虑）。当类型是oneToMany时，补填的是一个集合，因此值对象中也应当是一个集合，例如Customer中有一个Address是oneToMany：

```bash
/**
 * The customer entity
 * @author fangang
 */
public class Customer extends Entity<Long> {
	…
	private List<Address> addresses;
	
	/**
	 * @return the addresses
	 */
	public List<Address> getAddresses() {
		return addresses;
	}

	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
}
```
因此，在vObj.xml中进行如下配置：

```bash
  <vo class="com.demo2.trade.entity.Customer" tableName="Customer">
    <property name="id" column="id" isPrimaryKey="true"></property>
    <property name="name" column="name"></property>
    <property name="sex" column="sex"></property>
    <property name="birthday" column="birthday"></property>
    <property name="identification" column="identification"></property>
    <property name="phone_number" column="phone_number"></property>
    <join name="addresses" joinKey="customer_id" joinType="oneToMany" class="com.demo2.trade.entity.Address"></join>
  </vo>
```
### 2）内置聚合的实现
聚合是领域驱动设计中一个非常重要的概念，它代表在真实世界中的整体与部分的关系。比如，Order（订单）与OrderItem（订单明细）就是一个整体与部分的关系。当加载一个订单时应当同时加载其订单明细，而保存订单时应当同时保存订单与订单明细并放在同一事务中。本框架简化了聚合的设计实现。

当两个领域对象存在聚合关系时（如订单与订单明细），则在vObj.xml中建模时，通过join标签关联它们，并置join标签的isAggregation=true。这样，在查询或装载订单的同时装载它的所有订单明细，而在保存订单时保存订单明细，并将它们置于同一事务中。

### 3）通用DDD仓库与工厂
传统的领域驱动框架，每个业务模块都要编写自己的DDD仓库与工厂。但本框架为了简化领域驱动设计的设计，整个系统只使用一个通用的DDD仓库与工厂。DDD的通用工厂已经封装在了DDD仓库中，不需要使用者进行任何配置编码。DDD的通用仓库，实际上是BasicDao的一个装饰者，它实现了BasicDao的所有数据库持久化操作，但在这些操作的基础上实现了DDD所需的功能，如数据补填与内置聚合实现。除此之外，如果dao配置的是RepositoryWithCache，还可以实现Redis的缓存功能，即在加载或查询值对象以后，将缓存在Redis中，这样下一次查询时将不再查询数据库，而是从Redis中获取。

要使用Redis缓存，需要在application.yml（或properties）配置文件中加入Redis的配置：

```bash
spring:
  redis:
    database: 0
    host: 139.9.35.139
    port: 6379
    password: 
    pool:
      maxActive: 200
      maxWait: -1
      maxIdel: 10
      minIdel: 0
timeout: 1000
```
## 4. 支持微服务的技术框架
采用以上支持领域驱动的技术架构，在转型为微服务架构时还存在问题。比如，在加载Product时，通过join标签需要补填Supplier。而Supplier通过微服务拆分，可能在另一个微服务中，因此Product微服务通过数据库根本无法访问Supplier表。这时，通过join标签是没有办法完成Supplier的补填工作的。因此，本框架添加了ref标签。

为了使用ref标签，需要在Product微服务中添加Supplier接口并编写Feign注解：

```bash
/**
 * The service of suppliers.
 * @author fangang
 */
@FeignClient(value="service-supplier", fallback=SupplierHystrixImpl.class)
public interface SupplierService {
	/**
	 * @param id
	 * @return the supplier
	 */
	@RequestMapping(value = "orm/supplier/loadSupplier", method = RequestMethod.GET)
	public Supplier loadSupplier(@RequestParam("id")Long id);
	/**
	 * @param ids
	 * @return
	 */
	@PostMapping("orm/supplier/loadSuppliers")
	public List<Supplier> loadSuppliers(@RequestParam("ids")List<Long> ids);
	
	/**
	 * @return the list of supplier
	 */
	@GetMapping("orm/supplier/listOfSuppliers")
	public List<Supplier> listOfSuppliers();
}
```
通过该接口，Product微服务就可以远程调用Supplier微服务提供的API进行远程调用。接着，就可以在vObj.xml中通过ref标签进行建模：

```bash
  <vo class="com.demo2.trade.entity.Product" tableName="Product">
    <property name="id" column="id" isPrimaryKey="true"></property>
    <property name="name" column="name"></property>
    <property name="price" column="price"></property>
    <property name="unit" column="unit"></property>
    <property name="classify" column="classify"></property>
    <property name="supplier_id" column="supplier_id"></property>
    <ref name="supplier" refKey="supplier_id" refType="manyToOne" bean="com.demo2.product.service.SupplierService" method="loadSupplier" listMethod="loadSuppliers"></ref>
  </vo>
```
这里bean就是那个Feign接口。通过该配置，在装载或查询Product的时候，就会远程调用Supplier微服务完成信息的补填。
同时，Supplier微服务应当提供2个接口，一个是通过单个id进行查找，一个是通过多个id进行批量查找，以提升系统性能。
