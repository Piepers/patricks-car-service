<#include "header.ftl">

<div class="row">

    <div class="col-md-12 mt-1">
        <div class="float-xs-right">
            <form  action="/create" method="post">
                <div class="form-group">
                    <input type="text" class="form-control" id="name" name="name" placeholder="New car name">
                    <input type="text" class="form-control" id="type" name="type" placeholder="Type">
                    <input type="text" class="form-control" id="brand" name="brand" placeholder="Brand">
                    <input type="text" class="form-control" id="bhp" name="bhp" placeholder="Brake Horse Power">
                </div>
                <button type="submit" class="btn btn-primary">Create</button>
            </form>
        </div>
        <h1 class="display-4">${context.title}</h1>
    </div>

    <div class="col-md-12 mt-1">
  <#list context.cars>
      <h2>Cars:</h2>
      <ul>
      <#items as car>
          <li>${car}</li>
      </#items>
      </ul>
  <#else>
    <p>Patrick doesn't have any cars yet!</p>
  </#list>
    </div>

</div>

<#include "footer.ftl">