<#include "header.ftl">
<div class="row">
    <h1 class="display-4">${context.title}</h1>
</div>
<div class="row">
<#--<div class="col-md-12 mt-1">-->
    <div class="col">
        <form action="/api/delete" method="post">
  <#list context.cars>
      <h2>Cars:</h2>
      <ul>
      <#items as car>
          <li>${car.name} <input type="hidden" name="id" value="${car.id}"/>(
              <button type="submit" class="btn btn-link">delete</button>
              )
          </li>
      </#items>
      </ul>
  <#else>
    <p>Patrick doesn't have any cars yet!</p>
  </#list>
        </form>
    </div>

<#--<div class="col-md-12 mt-1">-->
    <div class="col">
        <div class="float-right">
            <form action="/api/create" method="post">
                <div class="form-group row">
                    <input type="text" class="form-control" id="name" name="name" placeholder="New car name">
                </div>
                <div class="form-group row">
                    <input type="text" class="form-control" id="brand" name="brand" placeholder="Brand">
                </div>
                <div class="form-group row">
                    <input type="text" class="form-control" id="type" name="type" placeholder="Type">
                </div>
                <div class="form-group row">
                    <input type="text" class="form-control" id="bhp" name="bhp" placeholder="Brake Horse Power">
                </div>
                <div class="form-group row">
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </form>
        </div>
    </div>
</div>

<#include "footer.ftl">