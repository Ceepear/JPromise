# JPromise
## 介绍
这是我闲暇之余在Java端完成的Promise框架，目前还有些许不足之处，希望大佬们能提些改进意见。
## 导包
  ```java
    import org.jpromise.*;
  ```
## Promise的创建
这是javascript创建promise的方式：
```javascript
   const p = new Promise((resolve,reject)=>{});
```
而java虽然是强类型语言，但是由于泛型和lambda表达式的存在，我们可以这么完成：
```java
   Promise<String> p = new Promise();
   p.call((resolve,reject)->{
        resolve.call(data)
   })
   问：为什么不直接使用 new Promise((resolve,reject)->{});
   答：因为对java泛型的使用不精，无法解决then链式调用时的类型问题，因此多走了一步call，并且由于引入了call，所以支持了Promise的复用。
```
### Promise的使用
大体上与javascript一致。
### 当前问题
1. 只能一个then一个then的按顺序执行，无法达到javascript上的按照延迟（delay）的时间来调用。<br>
2. 没有all等方法。<br>
3. 不是很灵活。<br>
4. 糟糕的代码。<br>
