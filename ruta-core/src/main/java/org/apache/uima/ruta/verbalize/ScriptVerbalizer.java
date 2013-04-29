/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.ruta.verbalize;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.uima.ruta.RutaBlock;
import org.apache.uima.ruta.RutaElement;
import org.apache.uima.ruta.RutaStatement;
import org.apache.uima.ruta.action.AbstractRutaAction;
import org.apache.uima.ruta.condition.AbstractRutaCondition;
import org.apache.uima.ruta.expression.RutaExpression;
import org.apache.uima.ruta.expression.number.NumberExpression;
import org.apache.uima.ruta.expression.type.TypeExpression;
import org.apache.uima.ruta.rule.ComposedRuleElement;
import org.apache.uima.ruta.rule.RegExpRule;
import org.apache.uima.ruta.rule.RuleElement;
import org.apache.uima.ruta.rule.RutaDisjunctiveMatcher;
import org.apache.uima.ruta.rule.RutaMatcher;
import org.apache.uima.ruta.rule.RutaRule;
import org.apache.uima.ruta.rule.RutaRuleElement;
import org.apache.uima.ruta.rule.WildCardRuleElement;
import org.apache.uima.ruta.rule.quantifier.MinMaxGreedy;
import org.apache.uima.ruta.rule.quantifier.MinMaxReluctant;
import org.apache.uima.ruta.rule.quantifier.NormalQuantifier;
import org.apache.uima.ruta.rule.quantifier.PlusGreedy;
import org.apache.uima.ruta.rule.quantifier.PlusReluctant;
import org.apache.uima.ruta.rule.quantifier.QuestionGreedy;
import org.apache.uima.ruta.rule.quantifier.QuestionReluctant;
import org.apache.uima.ruta.rule.quantifier.RuleElementQuantifier;
import org.apache.uima.ruta.rule.quantifier.StarGreedy;
import org.apache.uima.ruta.rule.quantifier.StarReluctant;

public class ScriptVerbalizer {

  private static final String THEN = " -> ";

  private RutaVerbalizer verbalizer;

  public ScriptVerbalizer(RutaVerbalizer verbalizer) {
    super();
    this.verbalizer = verbalizer;
  }

  public String verbalizeBlock(RutaBlock block, boolean withElements) {
    StringBuilder result = new StringBuilder();
    RutaRule rule = block.getRule();
    List<RutaStatement> elements = block.getElements();
    result.append("BLOCK(");
    result.append(block.getName());
    result.append(")");
    result.append(" ");
    if (rule != null) {
      result.append(verbalizeRule(rule));
    }
    if (withElements) {
      result.append(" {\n");
      for (RutaStatement each : elements) {
        if (each instanceof RutaBlock) {
          result.append(verbalizeBlock((RutaBlock) each, withElements));
        } else if (each instanceof RutaRule) {
          result.append(verbalizeRule((RutaRule) each));
        }
        result.append(";");
        result.append("\n");
      }
      result.append(" }\n");
    }
    return result.toString();
  }

  public String verbalizeRule(RutaRule rule) {
    List<RuleElement> elements = rule.getRuleElements();
    StringBuilder result = new StringBuilder();
    for (RuleElement each : elements) {
      result.append(verbalizeRuleElement(each));
      result.append(" ");
    }
    return result.toString();
  }

  public String verbalizeRuleElement(RuleElement re) {
    List<AbstractRutaCondition> conditions = re.getConditions();
    List<AbstractRutaAction> actions = re.getActions();
    RuleElementQuantifier quantifier = re.getQuantifier();
    StringBuilder result = new StringBuilder();
    if (re instanceof ComposedRuleElement) {
      result.append(verbalizeComposed((ComposedRuleElement) re));
    } else if (re instanceof RutaRuleElement) {
      RutaRuleElement tmre = (RutaRuleElement) re;
      result.append(verbalizeMatcher(tmre));
    } else if(re instanceof WildCardRuleElement) {
      result.append("#");
    }
    result.append(verbalizeQuantifier(quantifier));

    if (!conditions.isEmpty() || !actions.isEmpty()) {
      result.append("{");
      Iterator<AbstractRutaCondition> cit = conditions.iterator();
      while (cit.hasNext()) {
        AbstractRutaCondition each = cit.next();
        result.append(verbalizer.verbalize(each));
        if (cit.hasNext()) {
          result.append(",");
        }
      }
      if (!actions.isEmpty()) {
        result.append(THEN);
        Iterator<AbstractRutaAction> ait = actions.iterator();
        while (ait.hasNext()) {
          AbstractRutaAction each = ait.next();
          result.append(verbalizer.verbalize(each));
          if (ait.hasNext()) {
            result.append(",");
          }
        }
      }
      result.append("}");
    }
    return result.toString();
  }

  public String verbalizeComposed(ComposedRuleElement cre) {
    StringBuilder result = new StringBuilder();
    List<RuleElement> ruleElements = cre.getRuleElements();
    result.append("(");
    for (RuleElement each : ruleElements) {
      if (cre.getRuleElements().indexOf(each) != 0) {
        result.append(" ");
      }
      result.append(verbalizeRuleElement(each));
    }
    result.append(")");
    return result.toString();
  }

  public String verbalizeMatcher(RutaRuleElement tmre) {
    StringBuilder result = new StringBuilder();
    RutaMatcher matcher = tmre.getMatcher();
    if (matcher instanceof RutaDisjunctiveMatcher) {
      RutaDisjunctiveMatcher dmatcher = (RutaDisjunctiveMatcher) matcher;
      List<RutaExpression> expressions = dmatcher.getExpressions();
      result.append("(");
      for (RutaExpression each : expressions) {
        if (expressions.indexOf(each) != 0) {
          result.append(" | ");
        }
        result.append(verbalizer.verbalize(each));
      }
      result.append(")");
    } else {
      result.append(verbalizer.verbalize(matcher.getExpression()));
    }
    return result.toString();
  }

  public String verbalizeQuantifier(RuleElementQuantifier quantifier) {
    if (quantifier instanceof NormalQuantifier) {
      return "";
    } else if (quantifier instanceof MinMaxGreedy) {
      MinMaxGreedy mmg = (MinMaxGreedy) quantifier;
      return "[" + verbalizer.verbalize(mmg.getMin()) + "," + verbalizer.verbalize(mmg.getMax())
              + "]";
    } else if (quantifier instanceof MinMaxReluctant) {
      MinMaxReluctant mmr = (MinMaxReluctant) quantifier;
      return "[" + verbalizer.verbalize(mmr.getMin()) + "," + verbalizer.verbalize(mmr.getMax())
              + "]?";
    } else if (quantifier instanceof PlusGreedy) {
      return "+";
    } else if (quantifier instanceof PlusReluctant) {
      return "+?";
    } else if (quantifier instanceof QuestionGreedy) {
      return "?";
    } else if (quantifier instanceof QuestionReluctant) {
      return "??";
    } else if (quantifier instanceof StarGreedy) {
      return "*";
    } else if (quantifier instanceof StarReluctant) {
      return "*?";
    }
    return null;
  }

  public String verbalize(RutaElement element) {
    if (element instanceof RutaBlock) {
      return verbalizeBlock((RutaBlock) element, false);
    } else if (element instanceof RuleElementQuantifier) {
      return verbalizeQuantifier((RuleElementQuantifier) element);
    } else if (element instanceof RutaRule) {
      return verbalizeRule((RutaRule) element);
    } else if (element instanceof RegExpRule) {
      return verbalizeRegExpRule((RegExpRule) element);
    } else if (element instanceof RutaRuleElement) {
      return verbalizeRuleElement((RutaRuleElement) element);
    }
    return null;
  }

  private String verbalizeRegExpRule(RegExpRule rule) {
    StringBuilder sb = new StringBuilder();
    String regexp = verbalizer.verbalize(rule.getRegExp());
    sb.append(regexp);
    sb.append(THEN);

    Iterator<Entry<TypeExpression, NumberExpression>> iterator = rule.getTypeMap().entrySet()
            .iterator();
    while (iterator.hasNext()) {
      Entry<TypeExpression, NumberExpression> next = iterator.next();
      String type = verbalizer.verbalize(next.getKey());
      NumberExpression value = next.getValue();
      if (value != null) {
        String group = verbalizer.verbalize(value);
        sb.append(group+" = "+type);
      } else {
        sb.append(type);
      }
      if(iterator.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append(";");
    return sb.toString();
  }

}